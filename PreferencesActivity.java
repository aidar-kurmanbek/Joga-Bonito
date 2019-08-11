package kurmanbekuulua.jogabonito;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * Activity class for preferences screen.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */
public class PreferencesActivity extends AppCompatActivity {

    //fields
    private PreferencesManager pm;
    private CheckBox chkCoach;
    private RadioButton rbtnAZ;
    private CheckBox chkWarnDeleteTeam;
    private CheckBox chkWarnDeleteMatch;
    private CheckBox chkWarnDeletePlayer;

    /**
     * Android onClick method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //set up preferences screen
        pm = PreferencesManager.getInstance(getApplicationContext());
        chkCoach = findViewById(R.id.chkCoach);
        rbtnAZ = findViewById(R.id.rbtnAZ);
        RadioButton rbtnZA = findViewById(R.id.rbtnZA);
        chkWarnDeleteTeam = findViewById(R.id.chkWarnDeleteTeam);
        chkWarnDeleteMatch = findViewById(R.id.chkWarnDeleteMatch);
        chkWarnDeletePlayer = findViewById(R.id.chkWarnDeletePlayer);
        chkCoach.setChecked(pm.isListCoach());
        if (pm.isSortAZ()) {
            rbtnAZ.setChecked(true);
        } else {
            rbtnZA.setChecked(true);
        }
        chkWarnDeleteTeam.setChecked(pm.isWarnBeforeDeletingTeam());
        chkWarnDeleteMatch.setChecked(pm.isWarnBeforeDeletingMatch());
        chkWarnDeletePlayer.setChecked(pm.isWarnBeforeDeletingPlayer());
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Event handler for the save button click event.
     *
     * @param view the save button
     */
    public void btnSaveOnClick(View view) {
        pm.setListCoach(chkCoach.isChecked());
        pm.setSortAZ(rbtnAZ.isChecked());
        pm.setWarnBeforeDeletingTeam(chkWarnDeleteTeam.isChecked());
        pm.setWarnBeforeDeletingMatch(chkWarnDeleteMatch.isChecked());
        pm.setWarnBeforeDeletingPlayer(chkWarnDeletePlayer.isChecked());
        finish();
    }
}
