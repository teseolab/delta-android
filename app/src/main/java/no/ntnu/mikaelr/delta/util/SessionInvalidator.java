package no.ntnu.mikaelr.delta.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.view.LoginActivity;

public class SessionInvalidator {

    public static void invalidateSession(Activity context) {
        SharedPrefsUtil.getInstance().setCookie("");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        Toast.makeText(context, ErrorMessage.SESSION_EXPIRED, Toast.LENGTH_LONG).show();
    }

}
