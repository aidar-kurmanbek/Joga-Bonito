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
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for edit match screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class EditMatchesActivity extends BaseActivity {

    //fields
    private DataManager dm;
    private int matchId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private EditText txtVsName;
    private EditText txtDateTime;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_matches);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        matchId = intent.getIntExtra(Extras.MATCH_ID, -1);
        setTitle(R.string.titleEditMatch);
        if (matchId < 0) {
            setTitle(R.string.titleAddMatch);
        }
        txtVsName = findViewById(R.id.txtVsName);
        txtDateTime = findViewById(R.id.txtDateTime);
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
     * Sets up the data listener for the match object after authentication is completed.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        if (matchId >= 0) {
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
                        Matches thisMatch = snapshot.toObject(Matches.class);
                        dm.setMatches(thisMatch);
                        txtVsName.setText(thisMatch.getVsTeam());
                        txtDateTime.setText(thisMatch.getDateTime());
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
     * @return true
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

        String entry4 = txtVsName.getText().toString();
        if (TextUtils.isEmpty(entry4)) {
            txtVsName.setError("Opposition team name is required.");
            return;
        }
        String entry3 = txtDateTime.getText().toString();
        if (TextUtils.isEmpty(entry3)) {
            txtDateTime.setError("Date and time are required.");
            return;
        }
        //GUI fields needed
        EditText txtVsName = findViewById(R.id.txtVsName);
        EditText txtDateTime = findViewById(R.id.txtDateTime);

        //set up match object
        Matches v;
        boolean add = false;
        if (matchId > -1) {
            v = dm.getMatches(matchId);
        } else {
            v = new Matches();
            add = true;
        }
        //set matches fields
        v.setVsTeam(txtVsName.getText().toString());
        v.setDateTime(txtDateTime.getText().toString());
        //if new, save to list and return id
        if (add) {
            dm.addMatches(v);
            matchId = v.getMatchId();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Extras.MATCH_ID, matchId);
            setResult(RESULT_OK, resultIntent);
        }
        //close
        finish();
    }
}
