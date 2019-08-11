package kurmanbekuulua.jogabonito;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the user preferences for the app.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */

public class PreferencesManager {

    //fields
    private static PreferencesManager pm;
    private boolean listCoach;
    private boolean sortAZ;
    private boolean warnBeforeDeletingTeam;
    private boolean warnBeforeDeletingMatch;
    private boolean warnBeforeDeletingPlayer;
    private final SharedPreferences PREFS;

    /**
     * Singleton pattern preferences manager instance.
     *
     * @return the single instance
     */
    public static PreferencesManager getInstance(Context ctx) {
        if (pm == null) {
            pm = new PreferencesManager(ctx);
        }
        return pm;
    }

    /**
     * private constructor
     */
    private PreferencesManager(Context ctx) {
        PREFS = ctx.getSharedPreferences("kurmanbekuulua.jogabonito",Context.MODE_PRIVATE);
        listCoach = PREFS.getBoolean("listType", true);
        Team.listType = listCoach;
        sortAZ = PREFS.getBoolean("sortAZ", true);
        warnBeforeDeletingTeam = PREFS.getBoolean("warnBeforeDeletingTeam", true);
        warnBeforeDeletingMatch = PREFS.getBoolean("warnBeforeDeletingMatch", true);
        warnBeforeDeletingPlayer = PREFS.getBoolean("warnBeforeDeletingPlayer", true);
    }

    /**
     * Provides access to the preference to list the coach name with team name.
     *
     * @return true if coach should be listed, false if not
     */
    public boolean isListCoach() {
        return listCoach;
    }

    /**
     * Allows the preference to list the coach with the team name to be changed.
     *
     * @param listCoach true if the coach should be listed, false if not
     */
    public void setListCoach(boolean listCoach) {
        this.listCoach = listCoach;
        PREFS.edit().putBoolean("listType", listCoach).apply();
        Team.listType = listCoach;
    }

    /**
     * Provides access to the preference to sort teams A to Z or Z to A.
     *
     * @return true if teams should be sorted A to Z, false if Z to A
     */
    public boolean isSortAZ() {
        return sortAZ;
    }

    /**
     * Allows the preference to sort teams A to Z or Z to A to be changed.
     *
     * @param sortAZ true if teams should be sorted A to Z, false if Z to A
     */
    public void setSortAZ(boolean sortAZ) {
        this.sortAZ = sortAZ;
        PREFS.edit().putBoolean("sortAZ", sortAZ).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a team.
     *
     * @return true if a warning should be given, false if not
     */
    public boolean isWarnBeforeDeletingTeam() {
        return warnBeforeDeletingTeam;
    }

    /**
     * Allows the preference to warn before deleting a team to be changed.
     *
     * @param warnBeforeDeletingTeam true if a a warning should be given, false if not
     */
    public void setWarnBeforeDeletingTeam(boolean warnBeforeDeletingTeam) {
        this.warnBeforeDeletingTeam = warnBeforeDeletingTeam;
        PREFS.edit().putBoolean("warnBeforeDeletingTeam", warnBeforeDeletingTeam).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a match.
     *
     * @return true if a warning should be given, false if not
     */
    public boolean isWarnBeforeDeletingMatch() {
        return warnBeforeDeletingMatch;
    }

    /**
     * Allows the preference to warn before deleting a match to be changed.
     *
     * @param warnBeforeDeletingMatch true if a a warning should be given, false if not
     */
    public void setWarnBeforeDeletingMatch(boolean warnBeforeDeletingMatch) {
        this.warnBeforeDeletingMatch = warnBeforeDeletingMatch;
        PREFS.edit().putBoolean("warnBeforeDeletingMatch", warnBeforeDeletingMatch).apply();
    }

    /**
     * Provides access to the preference to warn before deleting a player.
     *
     * @return true if a warning should be given, false if not
     */
    public boolean isWarnBeforeDeletingPlayer() {
        return warnBeforeDeletingPlayer;
    }

    /**
     * Allows the preference to warn before deleting a player to be changed.
     *
     * @param warnBeforeDeletingPlayer true if a a warning should be given, false if not
     */
    public void setWarnBeforeDeletingPlayer(boolean warnBeforeDeletingPlayer) {
        this.warnBeforeDeletingPlayer = warnBeforeDeletingPlayer;
        PREFS.edit().putBoolean("warnBeforeDeletingPlayer", warnBeforeDeletingPlayer).apply();
    }
}
