package kurmanbekuulua.jogabonito;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for player details screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class PlayerDetailsActivity extends BaseActivity {

    //fields
    private int playerId;
    private DataManager dm;
    private Player thisPlayer;
    private PreferencesManager pm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private TextView lblLastName;
    private TextView lblFirstName;
    private TextView lblPosition;
    private TextView lblGoals;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_player_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current player
        Intent intent = getIntent();
        playerId = intent.getIntExtra(Extras.PLAYER_ID, -1);
        if (playerId < 0) {
            finish();
        }
        //find UI components
        lblLastName = findViewById(R.id.lblLastName);
        lblFirstName = findViewById(R.id.lblFirstName);
        lblPosition = findViewById(R.id.lblPlayerPosition);
        lblGoals = findViewById(R.id.lblGoals);
    }

    /**
     * On pause, data listener is stopped.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (reg != null && dataListener != null) {
            reg.remove();
        }
    }

    /**
     * Connects up the data listener once authentication is completed in the BaseActivity.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        final DocumentReference ref = db.collection("users").document(userId).
                collection("players").document(String.valueOf(playerId));
        dataListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MYLOG", "Player listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisPlayer = snapshot.toObject(Player.class);
                    dm.setPlayer(thisPlayer);
                    lblLastName.setText(thisPlayer.getLastName());
                    lblFirstName.setText(thisPlayer.getFirstName());
                    lblPosition.setText(thisPlayer.getPosition());
                    lblGoals.setText(String.valueOf(thisPlayer.getGoalScored()));
                }
            }
        };
        reg = ref.addSnapshotListener(dataListener);
    }
    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_details, menu);
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
        return true;
    }

    /**
     * Handles the top menu item selection.
     *
     * @param item the item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //back arrow
                finish();
                return true;
            case R.id.action_edit:
                editPlayer();
                return true;
            case R.id.action_add:
                addPlayer();
                return true;
            case R.id.action_delete:
                deletePlayer();
                return true;
            case R.id.action_settings:
                //settings menu option
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Edits the current player.
     */
    public void editPlayer() {
        Intent intent = new Intent(this, EditPlayerActivity.class);
        intent.putExtra(Extras.PLAYER_ID, playerId);
        startActivity(intent);
    }

    /**
     * Delete the current player.
     */
    public void deletePlayer() {
        if (pm.isWarnBeforeDeletingPlayer()) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this player?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deletePlayer(playerId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deletePlayer(playerId);
        }
    }

    /**
     * Add new player.
     */
    public void addPlayer() {
        Intent intent = new Intent(this, EditPlayerActivity.class);
        startActivity(intent);
    }
}
