package kurmanbekuulua.jogabonito;

import android.support.annotation.NonNull;

/**
 * This class represents one match
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */

public class Matches implements Comparable<Matches> {

    //fields
    private int matchId;
    private String vsTeam;
    private String dateTime;


    /**
     * Default constructor.
     */
    public Matches() {}

    /**
     * Complete constructor.
     *
     * @param vsTeam versus team name
     * @param dateTime date and time of the match
     *
     */
    public Matches(String vsTeam, String dateTime) {
        this.vsTeam = vsTeam;
        this.dateTime = dateTime;
    }

    /**
     * Provides access to the match ID.
     *
     * @return the match ID
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * Allows the match ID to be changed.
     *
     * @param matchId the match ID
     */
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * Provides access to the vs team name.
     *
     * @return the vs team's name
     */
    public String getVsTeam() {
        return vsTeam;
    }

    public void setVsTeam(String vsTeam) {
        this.vsTeam = vsTeam;
    }

    /**
     * Provides access to the date and time.
     *
     * @return the date and time
     */
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }



    /**
     * Provides a string representation of this Match object.
     *
     * @return the vs team name
     */
    @Override
    public String toString() {
        return vsTeam;
    }

    /**
     * Compares another object to this one to determine if they are the same.
     *
     * @param o the other object
     * @return true if the IDs are the same, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matches matches = (Matches) o;

        Matches other = (Matches) o;
        return (matchId == other.matchId);
    }

    /**
     * Used to determine if a list of Matches contains this match.
     *
     * @return the unique hashcode for this object
     */
    @Override
    public int hashCode() {
        int result = matchId;
        result = 31 * result + vsTeam.hashCode();
        result = 31 * result + dateTime.hashCode();
        return result;
    }
    /**
     * Used to sort a list of Matches.
     *
     * @param another Match to compare to this one
     * @return 0 if equal, -1 if the other comes before, and 1 if it comes after this one
     */
    @Override
    public int compareTo(@NonNull Matches another) {
        String thisMatch = vsTeam.toLowerCase();
        String anotherName = another.vsTeam.toLowerCase();
        return thisMatch.compareTo(anotherName);
    }
}
