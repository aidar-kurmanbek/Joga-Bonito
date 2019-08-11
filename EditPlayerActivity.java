package kurmanbekuulua.jogabonito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for edit player screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class EditPlayerActivity extends BaseActivity {

    //fields
    private DataManager dm;
    private int playerId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private EditText txtLastName;
    private EditText txtFirstName;
    private EditText txtPosition;
    private EditText txtGoals;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        playerId = intent.getIntExtra(Extras.PLAYER_ID, -1);
        setTitle(R.string.title_activity_edit_player);
        if (playerId < 0) {
            setTitle(R.string.titleAddPlayer);
        }
        txtLastName = findViewById(R.id.txtVsName);
        txtFirstName = findViewById(R.id.txtDateTime);
        txtPosition = findViewById(R.id.txtPosition);
        txtGoals = findViewById(R.id.txtGoalScored);
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
        if (playerId >= 0) {
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
                        Player thisPlayer = snapshot.toObject(Player.class);
                        dm.setPlayer(thisPlayer);
                        txtLastName.setText(thisPlayer.getLastName());
                        txtFirstName.setText(thisPlayer.getFirstName());
                        txtPosition.setText(thisPlayer.getPosition());
                        txtGoals.setText(thisPlayer.getGoalScored());
                    }
                }
            };
            reg = ref.addSnapshotListener(dataListener);
        }
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_details, menu);
        return true;
    }

    /**
     * Handles the top menu item selection.
     *
     * @param item the item selected
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //back arrow
                finish();
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
     * Event handler for Save button click event.
     *
     * @param view the save button
     */
    public void btnSaveOnClick(View view) {

        String entry = txtLastName.getText().toString();
        if (TextUtils.isEmpty(entry)) {
            txtLastName.setError("Name is required.");
            return;
        }
        entry = txtGoals.getText().toString();
        if (TextUtils.isEmpty(entry)) {
            txtGoals.setError("Number of goals is required field. Enter 0 if haven't scored yet");
            return;
        } else if (!TextUtils.isDigitsOnly(entry)) {
            txtGoals.setError("Goals must contain only digits.");
            return;
        }
        String entry4 = txtFirstName.getText().toString();
        if (TextUtils.isEmpty(entry4)) {
            txtFirstName.setError("Name is required.");
            return;
        }
        String entry3 = txtPosition.getText().toString();
        if (TextUtils.isEmpty(entry3)) {
            txtPosition.setError("Position is required.");
            return;
        }
        //GUI fields needed
        EditText txtLastName = findViewById(R.id.txtVsName);
        EditText txtFirstName = findViewById(R.id.txtDateTime);
        EditText txtPosition = findViewById(R.id.txtPosition);
        EditText txtGoals = findViewById(R.id.txtGoalScored);

        //set up player object
        Player c;
        boolean add = false;
        if (playerId > -1) {
            c = dm.getPlayer(playerId);
        } else {
            c = new Player();
            add = true;
        }
        //set player fields
        c.setLastName(txtLastName.getText().toString());
        c.setFirstName(txtFirstName.getText().toString());
        c.setPosition(txtPosition.getText().toString());
        c.setGoalScored(txtGoals.getText().toString());
        //if new, save to list and return id
        if (add) {
            dm.addPlayer(c);
            playerId = c.getPlayerId();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Extras.PLAYER_ID, playerId);
            setResult(RESULT_OK, resultIntent);
        }
        //close
        finish();
    }
}
