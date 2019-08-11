package kurmanbekuulua.jogabonito;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the list of Team objects.
 *
 * @author Aidar Kurmanbek-Uulu
 * @version 1.2 2/11/2018
 */

public class DataManager {

    //fields
    private static DataManager dm;
    private List<Team> teamList;
    private int nextTeamId;
    private List<Matches> matchesList;
    private int nextMatchId;
    private List<Player> playerList;
    private int nextPlayerId;
    private PreferencesManager pm;
    private static String userId;

    /*
     * Private constructor that will eventually be populated from a data store.
     */
    private DataManager(Context ctx) {
        pm = PreferencesManager.getInstance(ctx);
        teamList = new ArrayList<>();
        matchesList = new ArrayList<>();
        playerList = new ArrayList<>();
    }

    /**
     * Singleton implementation - returns the single instance of the DataManager class.
     */
    public static DataManager getDataManager(Context ctx, String userId) {
        DataManager.userId = userId;
        if (dm == null) {
            dm = new DataManager(ctx);
        }
        return dm;
    }
    /**
     * Provides access to a sorted list of all teams.
     *
     * @return List<Team> - the list of teams
     */

    public List<Team> getTeamList() {
        Collections.sort(teamList);
        if (!pm.isSortAZ()) {
            Collections.reverse(teamList);
        }
        return teamList;
    }

    /**
     * Replaces the team list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param list the new list of Team objects
     */
    public void setTeamList(List<Team> list) {
        Collections.sort(list);
        teamList = list;
    }

    /**
     * Provides access to one team.
     *
     * @param id the team's id number
     * @return the Team object
     */

    public Team getTeam(int id) {
        //search for id
        int index = -1;
        for (int i = 0; i < teamList.size(); i++) {
            Team t = teamList.get(i);
            if (t.getTeamId() == id) {
                index = i;
                break;
            }
        }
        //if found
        if (index >= 0) {
            return teamList.get(index);
        } else {
            return null;
        }
    }

    /**
     * Replaces a team object in the team list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param team the team object with which to replace the existing one
     */
    public void setTeam(Team team) {
        for (int i = 0; i < teamList.size(); i++) {
            if (teamList.get(i).getTeamId() == team.getTeamId()) {
                teamList.set(i, team);
            }
        }
    }

    /**
     * Replaces a team object in the team list when a user has updated details.
     *
     * @param team the team object with which to replace the existing one
     */
    public void replaceTeam(Team team) {
        for (int i = 0; i < teamList.size(); i++) {
            if (teamList.get(i).getTeamId() == team.getTeamId()) {
                teamList.set(i, team);
            }
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("teams")
                .document(String.valueOf(team.getTeamId())).set(team);
    }

    /**
     * Adds a Team object to the list of teams, maintaining sorted order.
     *
     * @param newTeam the new Team object
     */

    public void addTeam(Team newTeam) {
        if (newTeam == null) {
            return;
        }
        newTeam.setTeamId(nextTeamId);
        teamList.add(newTeam);
        nextTeamId++;
        Collections.sort(teamList);
        if (!pm.isSortAZ()) {
            Collections.reverse(teamList);
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("teams")
                .document(String.valueOf(newTeam.getTeamId())).set(newTeam);
    }

    /**
     * Deletes the given team from the list.
     *
     * @param id the id of the team to delete
     */

    public void deleteTeam(int id) {
        int index = -1;
        //find team  in the list
        for (int i = 0; i < teamList.size() &&  index < 0; i++) {
            Team t = teamList.get(i);
            if (t.getTeamId() == id) {
                index = i;
            }
        }
        //if found
        if (index >= 0) {
            //delete
            teamList.remove(index);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).collection("teams")
                    .document(String.valueOf(id)).delete();
        }
    }

    /**
     * Provides access to a sorted list of all matches.
     * Sorts in reverse if user preferences require it.
     *
     * @return List<Matches> - the list of matches
     */
    public List<Matches> getMatchesList() {
        Collections.sort(matchesList);
        return matchesList;
    }

    /**
     * Replaces the matches list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param list the new list of Matches objects
     */
    public void setMatchesList(List<Matches> list) {
        Collections.sort(list);
        matchesList = list;
        if (matchesList.size() > 0) {
            nextMatchId = matchesList.get(0).getMatchId();
            for (Matches m : matchesList) {
                if (m.getMatchId() > nextMatchId) {
                    nextMatchId = m.getMatchId();
                }
            }
            nextMatchId++;
        }
    }

    /**
     * Provides access to one match.
     *
     * @param id the match id number
     * @return the Match object
     */
    public Matches getMatches(int id) {
        //search for id
        int index = -1;
        for (int i = 0; i<matchesList.size(); i++) {
            Matches p = matchesList.get(i);
            if (p.getMatchId() == id) {
                index = i;
                break;
            }
        }
        //if found
        if (index >= 0) {
            return matchesList.get(index);
        } else {
            return null;
        }
    }

    /**
     * Replaces a match object in the match list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param matches the matches object with which to replace the existing one
     */
    public void setMatches(Matches matches) {
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).getMatchId() == matches.getMatchId()) {
                matchesList.set(i, matches);
            }
        }
    }

    /**
     * Replaces a match object in the match list when a user has updated details.
     *
     * @param matches the matches object with which to replace the existing one
     */
    public void replaceMatches(Matches matches) {
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).getMatchId() == matches.getMatchId()) {
                matchesList.set(i, matches);
            }
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("matches")
                .document(String.valueOf(matches.getMatchId())).set(matches);

    }
    /**
     * Adds a Match object to the list of matches, maintaining sorted order.
     *
     * @param newMatches the new Match object
     */
    public int addMatches(Matches newMatches) {
        if (newMatches == null) {
            return -1;
        }
        newMatches.setMatchId(nextMatchId);
        matchesList.add(newMatches);
        nextMatchId++;
        Collections.sort(matchesList);
        if (!pm.isSortAZ()) {
            Collections.reverse(matchesList);
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("matches")
                .document(String.valueOf(newMatches.getMatchId())).set(newMatches);

        return newMatches.getMatchId();
    }

    /**
     * Deletes the given match from the list.
     *
     * @param id the id of the match to delete
     */
    public void deleteMatches(int id) {
        int index = -1;
        //find match in list
        for (int i = 0; i<matchesList.size(); i++) {
            Matches p = matchesList.get(i);
            if (p.getMatchId() == id) {
                index = i;
                break;
            }
        }
        //if found
        if (index >= 0) {
            //delete
            matchesList.remove(index);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).collection("matches")
                    .document(String.valueOf(id)).delete();
        }
    }

    /**
     * Provides access to a sorted list of all players.
     * Sorts in reverse if user preferences require it.
     *
     * @return List<Player> - the list of players
     */
    public List<Player> getPlayerList() {
        Collections.sort(playerList);
        return playerList;
    }

    /**
     * Replaces the player list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param list the new list of Player objects
     */
    public void setPlayerList(List<Player> list) {
        Collections.sort(list);
        playerList = list;
        if (playerList.size() > 0) {
            nextPlayerId = playerList.get(0).getPlayerId();
            for (Player p : playerList) {
                if (p.getPlayerId() > nextPlayerId) {
                    nextPlayerId = p.getPlayerId();
                }
            }
            nextPlayerId++;
        }
    }

    /**
     * Provides access to one player.
     *
     * @param id the player's id number
     * @return the Player object
     */
    public Player getPlayer(int id) {
        //search for id
        int index = -1;
        for (int i = 0; i < playerList.size(); i++) {
            Player p = playerList.get(i);
            if (p.getPlayerId() == id) {
                index = i;
                break;
            }
        }
        //if found
        if (index >= 0) {
            return playerList.get(index);
        } else {
            return null;
        }
    }

    /**
     * Replaces a player object in the player list when Firebase has pushed an update to
     * an activity. Does not require updating the DB since it came from the DB.
     *
     * @param player the player object with which to replace the existing one
     */
    public void setPlayer(Player player) {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getPlayerId() == player.getPlayerId()) {
                playerList.set(i, player);
            }
        }
    }

    /**
     * Replaces a player object in the player list when a user has edited details.
     *
     * @param player the player object with which to replace the existing one
     */
    public void replacePlayer(Player player) {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getPlayerId() == player.getPlayerId()) {
                playerList.set(i, player);
            }
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("players")
                .document(String.valueOf(player.getPlayerId())).set(player);
    }
    /**
     * Adds a Player object to the list of players, maintaining sorted order.
     *
     * @param newPlayer the new Player object
     */
    public int addPlayer(Player newPlayer) {
        if (newPlayer == null) {
            return -1;
        }
        newPlayer.setPlayerId(nextPlayerId);
        playerList.add(newPlayer);
        nextPlayerId++;
        Collections.sort(playerList);
        if (!pm.isSortAZ()) {
            Collections.reverse(playerList);
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("players")
                .document(String.valueOf(newPlayer.getPlayerId())).set(newPlayer);
        return newPlayer.getPlayerId();
    }
    /**
     * Deletes the given player from the list.
     *
     * @param id the id of the player to delete
     */
    public void deletePlayer(int id) {
        int index = -1;
        //find player in list
        for (int i = 0; i<playerList.size(); i++) {
            Player p = playerList.get(i);
            if (p.getPlayerId() == id) {
                index = i;
                break;
            }
        }
        //if found
        if (index >= 0) {
            //delete
            playerList.remove(index);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).collection("players")
                    .document(String.valueOf(id)).delete();
        }
    }
}
