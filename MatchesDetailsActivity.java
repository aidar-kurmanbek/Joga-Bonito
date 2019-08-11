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
 * Activity class for match details screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class MatchesDetailsActivity extends BaseActivity {

    //fields
    private int matchId;
    private DataManager dm;
    private Matches thisMatch;
    private PreferencesManager pm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    TextView lblVsTeam;
    TextView lblDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_matches_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current match
        Intent intent = getIntent();
        matchId = intent.getIntExtra(Extras.MATCH_ID, -1);
        if (matchId < 0) {
            finish();
        }
        lblVsTeam = findViewById(R.id.lblName);
        lblDateTime = findViewById(R.id.lblStadiumPrompt);
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
                collection("matches").document(String.valueOf(matchId));
        dataListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MYLOG", "Matches listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisMatch = snapshot.toObject(Matches.class);
                    dm.setMatches(thisMatch);
                    lblVsTeam.setText(thisMatch.getVsTeam());
                    lblDateTime.setText(thisMatch.getDateTime());
                }
            }
        };
        reg = ref.addSnapshotListener(dataListener);
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
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
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
            case R.id.action_edit:
                editMatch();
                return true;
            case R.id.action_add:
                addMatch();
                return true;
            case R.id.action_delete:
                deleteMatch();
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
     * Edits the current match.
     */
    public void editMatch() {
        Intent intent = new Intent(this, EditMatchesActivity.class);
        intent.putExtra(Extras.MATCH_ID, matchId);
        startActivity(intent);
    }

    /**
     * Delete the current match.
     */
    public void deleteMatch() {
        if (pm.isWarnBeforeDeletingMatch()) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this match?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deleteMatches(matchId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deleteMatches(matchId);
        }
    }

    /**
     * Add new match.
     */
    public void addMatch() {
            Intent intent = new Intent(this, EditMatchesActivity.class);
            startActivity(intent);
    }

}
