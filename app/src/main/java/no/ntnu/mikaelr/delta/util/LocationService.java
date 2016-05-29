package no.ntnu.mikaelr.delta.util;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.view.MissionActivity;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient googleApiClient;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    private SQLiteDatabase database;

    private float goalLatitude;
    private float goalLongitude;
    private int currentTaskIndex;
    private int projectId;

    @Override
    public void onCreate() {
        super.onCreate();
        database = new DbHelper(LocationService.this).getWritableDatabase();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        projectId = intent.getIntExtra("projectId", -1);
        goalLatitude = intent.getFloatExtra("currentTaskLatitude", -1);
        goalLongitude = intent.getFloatExtra("currentTaskLongitude", -1);
        currentTaskIndex = intent.getIntExtra("currentTaskIndex", -1);
        listener = new MyLocationListener(this, goalLatitude, goalLongitude);
        Log.v("START_SERVICE", "DONE");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
        }
        googleApiClient.disconnect();
    }



    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (goalLatitude == -1 || goalLongitude == -1) {
            Log.w("LocationService", "Could not retrieve goal location from task");
        }

        else {

            // TODO: Note about permission
            // On Marshmallow, if permission is not granted, this service will not do anything
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(3000);
                locationRequest.setSmallestDisplacement(5);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class MyLocationListener implements LocationListener {

        private LocationService context;
        private Location goalLocation;

        public MyLocationListener(LocationService context, float goalLatitude, float goalLongitude) {
            this.context = context;
            this.goalLocation = new Location("");
            this.goalLocation.setLatitude(goalLatitude);
            this.goalLocation.setLongitude(goalLongitude);
        }

        public void onLocationChanged(final Location loc) {

            if (currentTaskIndex > 0) {
                ContentValues positionRow = new ContentValues();
                positionRow.put("project_id", projectId);
                positionRow.put("latitude", loc.getLatitude());
                positionRow.put("longitude", loc.getLongitude());
                database.insert("positions", null, positionRow);
                Log.i("Saved position: ", loc.getLatitude() + ", " + loc.getLongitude());
            }

//            String[] projection = {"latitude, longitude"};
//            Cursor cursor = database.query("positions", projection, null, null, null, null, null);
//            while (cursor.moveToNext()) {
//                double latitude = cursor.getDouble(0);
//                double longitude = cursor.getDouble(1);
//                Log.i("Position (service): ", latitude + ", " + longitude);
//            }
//            cursor.close();

            if (isBetterLocation(loc, previousBestLocation)) {

                float distanceToTaskLocation = loc.distanceTo(goalLocation);
                float radius = 30f;
                if (distanceToTaskLocation <= radius) {

                    Intent intent = new Intent(context, MissionActivity .class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

                    Notification notification  = new Notification.Builder(context)
                            .setContentTitle("Du er fremme!")
                            .setContentText("Trykk for å fortsette")
                            .setSmallIcon(R.drawable.ic_lightbulb_white_24dp)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.explore))
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);

                    context.stopSelf();
                }
            }
        }

    }
}