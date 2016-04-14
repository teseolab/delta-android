package no.ntnu.mikaelr.delta.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil extends Application {

    public static void saveMissionCompletionStatus(Activity context, int projectId, String value) {
        SharedPreferences preferences = context.getSharedPreferences("no.ntnu.mikaelr.delta", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PROJECT_" + projectId + "_MISSION_COMPLETED", value);
        editor.apply();
    }

}
