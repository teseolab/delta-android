package no.ntnu.mikaelr.delta.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HighscoreUser {

    private int id;
    private String username;
    private int score;
    private int numberOfMissions;
    private int numberOfSuggestions;
    private int numberOfComments;

    public static List<HighscoreUser> fromJson(JSONArray jsonArray) {

        List<HighscoreUser> users = new ArrayList<HighscoreUser>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonUser = jsonArray.getJSONObject(i);
                HighscoreUser user = new HighscoreUser();
                user.setId(jsonUser.getInt("id"));
                user.setUsername(jsonUser.getString("username"));
                user.setScore(jsonUser.getInt("score"));
                user.setNumberOfMissions(jsonUser.getInt("numberOfMissions"));
                user.setNumberOfSuggestions(jsonUser.getInt("numberOfSuggestions"));
                user.setNumberOfComments(jsonUser.getInt("numberOfComments"));
                users.add(user);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumberOfMissions() {
        return numberOfMissions;
    }

    public void setNumberOfMissions(int numberOfMissions) {
        this.numberOfMissions = numberOfMissions;
    }

    public int getNumberOfSuggestions() {
        return numberOfSuggestions;
    }

    public void setNumberOfSuggestions(int numberOfSuggestions) {
        this.numberOfSuggestions = numberOfSuggestions;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

}
