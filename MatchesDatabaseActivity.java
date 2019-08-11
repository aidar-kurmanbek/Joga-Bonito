package kurmanbekuulua.jogabonito;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
 * Activity class for matches list screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class MatchesDatabaseActivity extends BaseActivity {

    //fields
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<QuerySnapshot> dataListener;
    private ListenerRegistration reg;
    private DataManager dm;
    private ListView lstMatches;
    private EditText txtFilter;
    private ArrayAdapter<Matches> lstAdapter;
    private List<Matches> matchesList = new ArrayList<>();
    private boolean isFiltered;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //set up GUI components
        lstMatches = findViewById(R.id.lstPlayers);
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
        //set up match list
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        final CollectionReference ref = db.collection("users").document(userId)
                .collection("matches");
        dataListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    matchesList.clear();
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                        Matches matches = snapshot.toObject(Matches.class);
                        matchesList.add(matches);
                    }
                    dm.setMatchesList(matchesList);
                    //set up list view adapter
                    lstAdapter = new ArrayAdapter<>(MatchesDatabaseActivity.this,
                            android.R.layout.simple_list_item_1, matchesList);
                    lstMatches.setAdapter(lstAdapter);
                    //create list view click event
                    AdapterView.OnItemClickListener itemClickListener =
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Matches thisMatch = (Matches) lstMatches.getItemAtPosition(position);
                                    Intent intent = new Intent(MatchesDatabaseActivity.this,
                                            MatchesDetailsActivity.class);
                                    intent.putExtra(Extras.MATCH_ID, thisMatch.getMatchId());
                                    startActivity(intent);
                                    clearFilter();
                                }
                            };
                    lstMatches.setOnItemClickListener(itemClickListener);
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
     * @return true or false
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
     * Event handler for Filter button click event.
     *
     * @param view the Filter button
     */
    public void ibtnFilterOnClick(View view) {
        //hide keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(
                    (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //handle filter request
        if (txtFilter.getText().toString().isEmpty()) {
            clearFilter();
            isFiltered = false;
        } else {
            isFiltered = true;
            filterList();
        }
    }

    /**
     * Filters the list based on user selection.
     */
    public void filterList() {
        String filter = txtFilter.getText().toString().toLowerCase();
        //filter list
        List<Matches> filteredList = new ArrayList<>();
        for (Matches m : matchesList) {
            if ((m.getVsTeam().toLowerCase().contains(filter))) {
                filteredList.add(m);
            }
        }
        lstAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filteredList);
        lstMatches.setAdapter(lstAdapter);
        lstMatches.destroyDrawingCache();
        lstMatches.setVisibility(View.INVISIBLE);
        lstMatches.setVisibility(View.VISIBLE);
    }

    /**
     * Event handler for AddMatch button click event.
     *
     * @param view the add match button
     */
    public void btnAddMatchOnClick(View view) {
        Intent intent = new Intent(this, EditMatchesActivity.class);
        startActivity(intent);
        clearFilter();
    }
    /**
     * Clears the list filter.
     */
    public void clearFilter() {
        lstAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, matchesList);
        lstMatches.setAdapter(lstAdapter);
        lstMatches.destroyDrawingCache();
        lstMatches.setVisibility(View.INVISIBLE);
        lstMatches.setVisibility(View.VISIBLE);
    }
}
