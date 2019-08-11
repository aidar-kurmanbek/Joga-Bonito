package kurmanbekuulua.jogabonito;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Activity class for team details screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 1.1 2/11/2018
 */

public class TeamDetailsActivity extends BaseActivity {

    //fields
    private int teamId;
    private DataManager dm;
    private Team thisTeam;
    private TextView lblName;
    private TextView lblStadium;
    private TextView lblYear;
    private TextView lblCoach;
    private PreferencesManager pm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;


    /**
     * Builds the activity when first being created.
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_team_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current team
        Intent intent = getIntent();
        teamId = intent.getIntExtra(Extras.TEAM_ID, -1);
        if (teamId < 0) {
            finish();
        }
        //find UI components
        lblName = findViewById(R.id.lblName);
        lblStadium = findViewById(R.id.lblStadium);
        lblYear = findViewById(R.id.lblYear);
        lblCoach = findViewById(R.id.lblCoach);
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
     * Connects up the data listeners once authentication is completed in the BaseActivity.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        final DocumentReference ref = db.collection("users").document(userId).
                collection("teams").document(String.valueOf(teamId));
        dataListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MYLOG", "Team listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisTeam = snapshot.toObject(Team.class);
                    dm.setTeam(thisTeam);
                    lblName.setText(thisTeam.getTeamName());
                    lblStadium.setText(thisTeam.getStadium());
                    lblYear.setText(String.valueOf(thisTeam.getYear()));
                    lblCoach.setText(thisTeam.getCoach());
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
                finish();
                return true;
            case R.id.action_edit:
                editTeam();
                return true;
            case R.id.action_add:
                addTeam();
                return true;
            case R.id.action_delete:
                deleteTeam();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Edits the current team.
     */
    public void editTeam() {
        Intent intent = new Intent(this, EditTeamActivity.class);
        intent.putExtra(Extras.TEAM_ID, teamId);
        startActivity(intent);
    }

    /**
     * Delete the current team.
     */
    public void deleteTeam() {
        if (pm.isWarnBeforeDeletingTeam()) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this team?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deleteTeam(teamId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deleteTeam(teamId);
        }
    }

    /**
     * Add new team.
     */
    public void addTeam() {
        Intent intent = new Intent(this, EditTeamActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the current squad click event.
     *
     * @param view the current squad button
     */
    public void btnCurrentSquadOnClick(View view) {
        Intent intent = new Intent(this, PlayerDatabaseActivity.class);
        startActivity(intent);
    }
    /**
     * Handles the matches click event.
     *
     * @param view the matches button
     */
    public void btnMatchesOnClick(View view) {
        Intent intent = new Intent(this, MatchesDatabaseActivity.class);
        startActivity(intent);
    }
}
