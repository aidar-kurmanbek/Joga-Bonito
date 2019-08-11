package kurmanbekuulua.jogabonito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class for team database.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 1.1 1/28/2018
 */

public class TeamDatabaseActivity extends BaseActivity {

    //fields
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<QuerySnapshot> dataListener;
    private ListenerRegistration reg;
    private DataManager dm;
    private ListView lstTeams;
    private EditText txtFilter;
    private ArrayAdapter<Team> lstAdapter;
    private List<Team> teamList = new ArrayList<>();
    private boolean isFiltered;
    public static final String TEAM_ID = "teamId";

    /**
     * Builds the activity on startup and sets up the data manager.
     *
     * @param savedInstanceState the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_database);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //set up GUI components
        lstTeams = findViewById(R.id.lstTeams);
        txtFilter = findViewById(R.id.txtFilter);
        //get saved state
        if (savedInstanceState != null) {
            isFiltered = savedInstanceState.getBoolean("isFiltered");
        }
    }

    /**
     * Saves the state of the activity.
     *
     * @param outState the class state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFiltered", isFiltered);
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
     * On resume, set up the list adapter and filter if was filtered.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (lstAdapter != null) {
            lstAdapter.notifyDataSetChanged();
        }
        if (isFiltered) {
            filterList();
        }
    }

    /**
     * Connects up the data listener once authentication is completed in the BaseActivity.
     */
    @Override
    public void setUpDataListeners() {
        //set up team list
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        final CollectionReference ref = db.collection("users").document(userId)
                .collection("teams");
        dataListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    teamList.clear();
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                        Team team = snapshot.toObject(Team.class);
                        teamList.add(team);
                    }
                    dm.setTeamList(teamList);
                    //set up list view adapter
                    lstAdapter = new ArrayAdapter<>(TeamDatabaseActivity.this,
                            android.R.layout.simple_list_item_1, teamList);
                    lstTeams.setAdapter(lstAdapter);
                    //create list view click event
                    AdapterView.OnItemClickListener itemClickListener =
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Team thisTeam = (Team) lstTeams.getItemAtPosition(position);
                                    Intent intent = new Intent(TeamDatabaseActivity.this, TeamDetailsActivity.class);
                                    intent.putExtra(Extras.TEAM_ID, thisTeam.getTeamId());
                                    startActivity(intent);
                                    clearFilter();
                                    isFiltered = false;
                                }
                            };
                    lstTeams.setOnItemClickListener(itemClickListener);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Filters the list based on user selection.
     */
    public void filterList() {
        if (dm == null) {
            return;
        }
        String filter = txtFilter.getText().toString().toLowerCase();
        //filter list
        List<Team> filteredList = new ArrayList<>();
        for (Team t : teamList) {
            if ((t.getTeamName().toLowerCase().contains(filter))
                    || (t.getPlayerId() > -1 && dm.getPlayer(t.getPlayerId()).toString().toLowerCase().contains(filter))
                    || (t.getMatchId() > -1 && dm.getMatches(t.getMatchId()).toString().toLowerCase().contains(filter))) {
                filteredList.add(t);
            }
        }
        lstAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filteredList);
        lstTeams.setAdapter(lstAdapter);
        lstTeams.destroyDrawingCache();
        lstTeams.setVisibility(View.INVISIBLE);
        lstTeams.setVisibility(View.VISIBLE);
    }

    /**
     * Event handler for Overview button click event.
     *
     * @param view the overview button
     */
    public void btnOverviewOnClick(View view) {
        ListView lstTeams = findViewById(R.id.lstTeams);
        Team thisTeam = (Team) lstTeams.getSelectedItem();
        if (thisTeam != null) {
            Intent intent = new Intent(this, TeamDetailsActivity.class);
            intent.putExtra(TEAM_ID, thisTeam.getTeamId());
            startActivity(intent);
            clearFilter();
        }
    }

    /**
     * Event handler for Search button click event.
     *
     * @param view the search button
     */
    public void ibtnSearchOnClick(View view) {
        List<Team> filteredList = new ArrayList<>();
        ListView lstTeams = findViewById(R.id.lstTeams);
        EditText txtFilter = findViewById(R.id.txtFilter);
        String filterText = txtFilter.getText().toString();
        if (filterText.length() > 0) {
            //filter list
            for (Team t : teamList) {
                if (t.toString().toLowerCase().contains(txtFilter.getText().toString().toLowerCase())) {
                    filteredList.add(t);
                }
            }
            ArrayAdapter<Team> spnAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, filteredList);
            spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lstTeams.setAdapter(spnAdapter);
        } else {
            clearFilter();
        }
    }

    /**
     * Event handler for AddTeam button click event.
     *
     * @param view the add button
     */
    public void btnAddTeamOnClick(View view) {
        Intent intent = new Intent(this, EditTeamActivity.class);
        startActivity(intent);
        clearFilter();
    }

    /**
     * Clears the filter settings and displays the entire list again.
     */
    public void clearFilter() {
        ListView lstTeams = findViewById(R.id.lstTeams);
        EditText txtFilter = findViewById(R.id.txtFilter);
        ArrayAdapter<Team> spnAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teamList);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lstTeams.setAdapter(spnAdapter);
        txtFilter.setText("");
    }

    public void btnSignOutOnClick(View view) {
        dm.getTeamList().clear();
        signOut();
    }
}
