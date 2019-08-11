package kurmanbekuulua.jogabonito;

import android.support.annotation.NonNull;

/**
 * This class represents one player.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 2/11/2018
 */

public class Player implements Comparable<Player> {

    //fields
    private int playerId;
    private String lastName;
    private String firstName;
    private String position;
    private String goalScored;

    /**
     * Default constructor.
     */
    public Player(){
    }

    /**
     * Complete constructor
     *
     * @param lastName player's last name
     * @param firstName player's first name
     * @param position player's field position
     * @param goalScored player's goal tally
     */
    public Player(String lastName, String firstName, String position, String goalScored) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.goalScored = goalScored;
    }

    /**
     * Provides access to the ID of the player.
     *
     * @return the player ID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Allows the ID of the player to be changed.
     *
     * @param playerId the player ID
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Provides access to the player's first name.
     *
     * @return the player's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Allows the player's first name to be changed.
     *
     * @param firstName the player's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Provides access to the player's last name.
     *
     * @return the player's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Allows the player's last name to be changed.
     *
     * @param lastName the player's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Provides access to the player's preferred position.
     *
     * @return the player's position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Allows the player's position to be changed.
     *
     * @param position the player's position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Provides access to the player's goal tally
     *
     * @return the player's goal tally
     */
    public String getGoalScored() {
        return goalScored;
    }
    /**
     * Allows the player's goals count to be changed.
     *
     * @param goalScored the player's goals count
     */
    public void setGoalScored(String goalScored) {
        this.goalScored = goalScored;
    }

    /**
     * Creates a string representation of this Player object.
     *
     * @return the Player's full name
     */
    @Override
    public String toString() {
        return (firstName + " " + lastName);
    }

    /**
     * Used to tell if one Player object reference is the same as another.
     *
     * @param o the object to compare to this one
     * @return true if the object has the same ID, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player other = (Player) o;
        return (playerId == other.playerId);
    }
    /**
     * Used to find this Player object in a list.
     *
     * @return the unique hashcode for this object
     */
    @Override
    public int hashCode() {
        int result = playerId;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (goalScored != null ? goalScored.hashCode() : 0);
        return result;
    }
    /**
     * Used to sort a list of Player objects.
     *
     * @param another the other Player object to compare this one
     * @return 0 if equal, -1 if the other comes before, and 1 if it comes after this one
     */
    @Override
    public int compareTo(@NonNull Player another) {
        String thisName = lastName.toLowerCase() + firstName.toLowerCase();
        String anotherName = another.lastName.toLowerCase() + another.firstName.toLowerCase();
        return thisName.compareTo(anotherName);
    }
}
