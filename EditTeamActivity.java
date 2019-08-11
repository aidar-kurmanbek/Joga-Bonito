package kurmanbekuulua.jogabonito;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for editing and adding teams.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 1.2 2/11/2018
 */

public class EditTeamActivity extends BaseActivity {

    //fields
    private DataManager dm;
    private int teamId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private EditText txtName;
    private EditText txtStadium;
    private EditText txtYear;
    private EditText txtCoach;

    /**
     * Builds the activity on startup.
     *
     * @param savedInstanceState the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        teamId = intent.getIntExtra(Extras.TEAM_ID, -1);
        setTitle(R.string.title_activity_edit_team);
        if (teamId < 0) {
            setTitle(R.string.title_activity_team_database);
        }
        txtName = findViewById(R.id.txtVsName);
        txtStadium = findViewById(R.id.txtDateTime);
        txtYear = findViewById(R.id.txtPosition);
        txtCoach = findViewById(R.id.txtGoalScored);
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
        if (teamId >= 0) {
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
                        Team thisTeam = snapshot.toObject(Team.class);
                        dm.setTeam(thisTeam);
                        txtName.setText(thisTeam.getTeamName());
                        txtStadium.setText(thisTeam.getStadium());
                        txtYear.setText(String.valueOf(thisTeam.getYear()));
                        txtCoach.setText(thisTeam.getCoach());
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
     * Event handler for Submit button click event.
     *
     * @param view the submit button
     */
    public void btnSubmitOnClick(View view) {

        String entry = txtName.getText().toString();
        if (TextUtils.isEmpty(entry)) {
            txtName.setError("Name is required.");
            return;
        }
        entry = txtYear.getText().toString();
        if (TextUtils.isEmpty(entry)) {
            txtYear.setError("Year is required.");
            return;
        } else if (!TextUtils.isDigitsOnly(entry)) {
            txtYear.setError("Year must contain only digits.");
            return;
        }
        String entry2 = txtStadium.getText().toString();
        if (TextUtils.isEmpty(entry2)) {
            txtStadium.setError("Stadium name is required.");
            return;
        }
        String entry3 = txtCoach.getText().toString();
        if (TextUtils.isEmpty(entry3)) {
            txtCoach.setError("Coach name is required.");
            return;
        }

        Team t;
        //add or edit?
        boolean add = false;
        if (teamId > -1) {
            t = dm.getTeam(teamId);
        } else {
            t = new Team();
            add = true;
        }
        //name
        EditText txtName = findViewById(R.id.txtVsName);
        t.setTeamName(txtName.getText().toString());
        //year
        EditText txtYear = findViewById(R.id.txtPosition);
        t.setYear(Integer.valueOf(txtYear.getText().toString()));
        //stadium
        EditText txtStadium = findViewById(R.id.txtDateTime);
        t.setStadium(txtStadium.getText().toString());
        //coach
        EditText txtCoach = findViewById(R.id.txtGoalScored);
        t.setCoach(txtCoach.getText().toString());

        //save to list
        if (add) {
            dm.addTeam(t);
            teamId = t.getTeamId();
        }
        //hide keyboard - return
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
        finish();

    }
}
