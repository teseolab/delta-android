package no.ntnu.mikaelr.delta.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HighscoreUser implements Serializable {

    private int id;
    private String username;
    private String avatarUri;
    private int score;
    private int numberOfMissions;
    private int numberOfSuggestions;
    private int numberOfComments;

    public static List<HighscoreUser> fromJsonArray(JSONArray jsonArray) {

        List<HighscoreUser> users = new ArrayList<HighscoreUser>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonUser = jsonArray.getJSONObject(i);
                HighscoreUser user = fromJsonObject(jsonUser);
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static HighscoreUser fromJsonObject(JSONObject jsonObject) {
        HighscoreUser user = new HighscoreUser();
        try {
            user.setId(jsonObject.getInt("id"));
            user.setUsername(jsonObject.getString("username"));
            String avatarUri = jsonObject.getString("avatarUri");
            if (avatarUri.equals("null") || avatarUri.equals("")) {
                user.setAvatarUri(null);
            } else {
                user.setAvatarUri(avatarUri);
            }
            user.setScore(jsonObject.getInt("score"));
            user.setNumberOfMissions(jsonObject.getInt("numberOfMissions"));
            user.setNumberOfSuggestions(jsonObject.getInt("numberOfSuggestions"));
            user.setNumberOfComments(jsonObject.getInt("numberOfComments"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
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

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
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
