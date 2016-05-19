package no.ntnu.mikaelr.delta.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {

    private static SharedPrefsUtil instance;
    private static SharedPreferences sharedPreferences;

    public static SharedPrefsUtil getInstance() {
        if (instance == null) {
            instance = new SharedPrefsUtil();
        }
        return instance;
    }

    public static void initSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("no.ntnu.mikaelr.delta", Context.MODE_PRIVATE);
    }

    public void setCookie(String cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Cookie", cookie);
        editor.apply();
    }

    public String getCookie() {
        return sharedPreferences.getString("Cookie", "");
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Username", username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString("Username", "");
    }

    public void setMissionCompletionStatus(int projectId, String username, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PROJECT_" + projectId + "_MISSION_COMPLETED_BY_USER_" + username, value);
        editor.apply();
    }

    public String getMissionCompletionStatus(int projectId, String username) {
        return sharedPreferences.getString("PROJECT_" + projectId + "_MISSION_COMPLETED_BY_USER_" + username, Constants.NA);
    }

    public void setStartLocationFoundStatus(Integer projectId, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PROJECT_" + projectId + "_START_LOCATION_FOUND_BY_USER_" + getUsername(), value);
        editor.apply();
    }

    public String getStartLocationFoundStatus(int projectId) {
        return sharedPreferences.getString("PROJECT_" + projectId + "_START_LOCATION_FOUND_BY_USER_" + getUsername(), Constants.NO);
    }

    public void setLocationFoundStatus(int projectId, int taskIndex, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PROJECT_" + projectId + "_TASK_NUMBER_" + taskIndex + "_LOCATION_FOUND_BY_USER_" + getUsername(), value);
        editor.apply();
    }

    public String getLocationFoundStatus(int projectId, int taskIndex) {
        return sharedPreferences.getString("PROJECT_" + projectId + "_TASK_NUMBER_" + taskIndex + "_LOCATION_FOUND_BY_USER_" + getUsername(), Constants.NO);
    }
}
