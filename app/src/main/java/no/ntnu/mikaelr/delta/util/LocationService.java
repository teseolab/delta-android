package no.ntnu.mikaelr.delta.util;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.view.MainActivity;
import no.ntnu.mikaelr.delta.view.MissionActivity;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("START_SERVICE", "DONE");

        float goalLatitude = intent.getFloatExtra("currentTaskLatitude", -1);
        float goalLongitude = intent.getFloatExtra("currentTaskLongitude", -1);

        if (goalLatitude == -1 || goalLongitude == -1) {
            System.out.println("Could not retrieve goal location from task");
        }

        else {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener(this, goalLatitude, goalLongitude);

            // TODO: Handle permissions on Marshmallow
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        // TODO: Handle permissions on Marshmallow
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(listener);
        }
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

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }
}