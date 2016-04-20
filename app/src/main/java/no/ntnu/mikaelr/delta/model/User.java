package no.ntnu.mikaelr.delta.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public static String userOut(String username, String password, String registerCode) {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("username", username);
            jsonUser.put("password", password);
            jsonUser.put("registerCode", registerCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonUser.toString();
    }

    public static User userIn(String json) {
        try {
            JSONObject jsonUser = new JSONObject(json);
            return new User(jsonUser.getInt("id"), jsonUser.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
}
