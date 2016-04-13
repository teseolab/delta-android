package no.ntnu.mikaelr.delta.util;

import android.util.Log;

public class GeofencingReceiver extends ReceiveGeofenceTransitionIntentService {
    @Override
    protected void onEnteredGeofences(String[] strings) {
        Log.d(GeofencingReceiver.class.getName(), "onEnter");
        System.out.println("onEnter!!!!!-----------------------------------------------------------------------");
        //do something!
    }

    @Override
    protected void onExitedGeofences(String[] strings) {
        Log.d(GeofencingReceiver.class.getName(), "onExit");
        System.out.println("onExit!!!!!------------------------------------------------------------------------");
        //do something!
        }

    @Override
    protected void onError(int errorCode) {
        Log.e(GeofencingReceiver.class.getName(), "Error: " + errorCode);
    }

}