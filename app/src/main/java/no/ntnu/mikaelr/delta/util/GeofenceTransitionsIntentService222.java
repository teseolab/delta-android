package no.ntnu.mikaelr.delta.util;

import android.app.IntentService;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsIntentService222 extends IntentService {


    public GeofenceTransitionsIntentService222() {
        super(GeofenceTransitionsIntentService222.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
        } else {

            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Toast.makeText(this, "ENTERING GEOFENCE", Toast.LENGTH_SHORT).show();
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                // Delete the data item when leaving a geofence region.
                Toast.makeText(this, "EXITING GEOFENCE", Toast.LENGTH_SHORT).show();
            }
        }
    }

}