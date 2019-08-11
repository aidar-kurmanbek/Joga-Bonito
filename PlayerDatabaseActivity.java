package kurmanbekuulua.jogabonito;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
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
 * Activity class for player list screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class PlayerDatabaseActivity extends BaseActivity {

    //fields
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<QuerySnapshot> dataListener;
    private ListenerRegistration reg;
    private DataManager dm;
    private ListView lstPlayers;
    private EditText txtFilter;
    private ArrayAdapter<Player> lstAdapter;
    private List<Player> playerList = new ArrayList<>();
    boolean isFiltered;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_database);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //set up GUI components
        lstPlayers = findViewById(R.id.lstPlayers);
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
     * Sets up the data listener for the player list after authentication is complete.
     */
    @Override
    public void setUpDataListeners() {
        //set up players list
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        playerList = dm.getPlayerList();
        final CollectionReference ref = db.collection("users").document(userId)
                .collection("players");
        dataListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    playerList.clear();
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                        Player player = snapshot.toObject(Player.class);
                        playerList.add(player);
                    }
                    dm.setPlayerList(playerList);
                    //set up list view adapter
                    lstAdapter = new ArrayAdapter<>(PlayerDatabaseActivity.this,
                            android.R.layout.simple_list_item_1, playerList);
                    lstPlayers.setAdapter(lstAdapter);//set up list view adapter
                    lstAdapter = new ArrayAdapter<>(PlayerDatabaseActivity.this,
                            android.R.layout.simple_list_item_1, playerList);
                    lstPlayers.setAdapter(lstAdapter);
                    //create list view click event
                    AdapterView.OnItemClickListener itemClickListener =
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Player thisPlayer = (Player) lstPlayers.getItemAtPosition(position);
                                    Intent intent = new Intent(PlayerDatabaseActivity.this,
                                            PlayerDetailsActivity.class);
                                    intent.putExtra(Extras.PLAYER_ID, thisPlayer.getPlayerId());
                                    startActivity(intent);
                                    clearFilter();
                                }
                            };
                    lstPlayers.setOnItemClickListener(itemClickListener);
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
                NavUtils.navigateUpFromSameTask(this);
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
     * Event handler for Filter button click event.
     *
     * @param view the filter button
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
        List<Player> filteredList = new ArrayList<>();
        for (Player c : playerList) {
            if ((c.getLastName().toLowerCase().contains(filter))
                    || (c.getFirstName().toLowerCase().contains(filter))) {
                filteredList.add(c);
            }
        }
        lstAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filteredList);
        lstPlayers.setAdapter(lstAdapter);
        lstPlayers.destroyDrawingCache();
        lstPlayers.setVisibility(View.INVISIBLE);
        lstPlayers.setVisibility(View.VISIBLE);
    }
    /**
     * Event handler for AddPlayer button click event.
     *
     * @param view the add button
     */
    public void btnAddPlayerOnClick(View view) {
        Intent intent = new Intent(this, EditPlayerActivity.class);
        startActivity(intent);
        clearFilter();
    }

    /**
     * Clears the list filter.
     */
    public void clearFilter() {
        lstAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, playerList);
        lstPlayers.setAdapter(lstAdapter);
        lstPlayers.destroyDrawingCache();
        lstPlayers.setVisibility(View.INVISIBLE);
        lstPlayers.setVisibility(View.VISIBLE);
    }
}
