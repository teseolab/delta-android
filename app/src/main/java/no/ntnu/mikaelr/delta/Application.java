package no.ntnu.mikaelr.delta;

import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsUtil.initSharedPreferences(this);
    }

}
