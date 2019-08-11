package kurmanbekuulua.jogabonito;

import android.support.annotation.NonNull;

/**
 *
 * This class represents one team
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 1.1 1/28/2018
 */

public class Team implements Comparable<Team> {

    private int teamId;
    private String teamName;
    private String stadium;
    private int year;
    private String coach;
    private int playerId = -1;
    private int matchId = -1;
    public static boolean listType;



    /**
     * Default constructor. Needs access to the application context in order to use
     * strings.xml resources.
     */
    public Team() {
    }

    /**
     * Full constructor. Needs access to the application context in order to use
     * strings.xml resources.
     *
     * @param teamName the name of the team
     * @param stadium the stadium where team plays
     * @param year year when team was established
     * @param coach head coach of the team
     * @param matchId the id of the team's match
     * @param playerId the id of the team's player
     *
     */
    public Team(String teamName, String stadium, int year, String coach, int matchId, int playerId) {
        this.teamName = teamName;
        this.stadium = stadium;
        this.year = year;
        this.coach = coach;
        this.matchId = matchId;
        this.playerId = playerId;
    }

    /**
     * Temporary method to create a test team object.
     *
     * @return the team object
     */
    public static Team getTestTeam() {
        Team t = new Team();
        t.teamName = "Seattle Sounders FC";
        t.stadium = "Century Link Stadium";
        t.year = 1974;
        t.coach = "Brian Test";
        return t;
    }

    /**
     * Provides access to the team's unique ID in the system. The ID is set by the data manager.
     *
     * @return the team's ID
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Allows the team's unique ID in the system to be changed. This should only be used by
     * the data manager.
     *
     * @param teamId the team's ID
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Provides access to the team's name.
     *
     * @return the team's name
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Allows the team's name to be changed.
     *
     * @param teamName the team's name
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Provides access to the team's stadium name.
     *
     * @return the team's stadium name
     */
    public String getStadium() {
        return stadium;
    }


    /**
     * Allows the teams's stadium name to be changed.
     *
     * @param stadium the team's stadium
     */
    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    /**
     * Provides access to the year of establishment of the team.
     *
     * @return the year established
     */

    public int getYear() {
        return year;
    }

    /**
     * Allows the year of establishment of the team to be changed.
     *
     * @param year the team's s
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Provides access to the team's head coach name.
     *
     * @return the team's head coach name
     */
    public String getCoach() {
        return coach;
    }

    /**
     * Allows the teams's head coach name to be changed.
     *
     * @param coach the team's head coach
     */
    public void setCoach(String coach) {
        this.coach = coach;
    }

    /**
     * Provides access to the match of the team.
     *
     * @return the id of the match
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * Allows the match of the team to be changed.
     *
     * @param matchId the id of the match
     */
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * Provides access to the team's player.
     *
     * @return the id of the player object
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Allows the team's player to be changed.
     *
     * @param playerId the id of the player
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Provides a String representation of this team object.
     *
     * @return a String representation of this team
     */
    @Override
    public String toString() {
        String teamStr = teamName;
        if (listType) {
            teamStr += " - " + coach;
        }
        return teamStr;

    }

    /**
     * Determines if one Team object is equal to another, based on the team's ID
     *
     * @param o the other team
     * @return true if they both have the same ID, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (teamId != team.teamId) return false;
        if (year != team.year) return false;
        if (teamName != null ? !teamName.equals(team.teamName) : team.teamName != null)
            return false;
        if (stadium != null ? !stadium.equals(team.stadium) : team.stadium != null) return false;
        return coach != null ? coach.equals(team.coach) : team.coach == null;
    }

    /**
     * Provides a unique number to identify this object. Uses the team's ID.
     *
     * @return the team's ID
     */
    @Override
    public int hashCode() {
        int result = teamId;
        result = 31 * result + (teamName != null ? teamName.hashCode() : 0);
        result = 31 * result + (stadium != null ? stadium.hashCode() : 0);
        result = 31 * result + year;
        result = 31 * result + (coach != null ? coach.hashCode() : 0);
        return result;
    }

    public int compareTo(@NonNull Team other) {
        return this.toString().compareTo(other.toString());
    }
}
