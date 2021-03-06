package no.ntnu.mikaelr.delta.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.ui.IconGenerator;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.MissionPresenter;
import no.ntnu.mikaelr.delta.presenter.MissionPresenterImpl;
import no.ntnu.mikaelr.delta.util.LocationService;
import no.ntnu.mikaelr.delta.view.signature.MissionView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;

public class MissionActivity extends AppCompatActivity implements MissionView, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, CustomDialog.CustomDialogPositiveButtonListener {

    private static final String TAG = "MissionActivity";

    private MissionPresenter presenter;

    private GoogleMap map;
    private List<Marker> markers = new ArrayList<Marker>();
    private LatLngBounds.Builder boundsBuilder;

    private HashMap<String, Integer> markerIdsAndTaskIds = new HashMap<String, Integer>();

    // LIFECYCLE METHODS -----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        this.presenter = new MissionPresenterImpl(this);
        String missionName = presenter.getProject().getName() + " oppdrag";
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, missionName);
        presenter.connectApiClient();
        initializeMap();
    }

    @Override
    protected void onDestroy() {
        if (isMyServiceRunning(LocationService.class)) {
            presenter.stopLocationService();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (presenter.googleApiClientIsConnected()) {
            presenter.startLocationUpdates();
        } else {
            presenter.connectApiClient();
        }

        if (isMyServiceRunning(LocationService.class)) {
            presenter.stopLocationService();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        presenter.disconnectApiClient();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_mission, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mission_map);
        mapFragment.getMapAsync(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    private void setMyLocationEnabled(boolean enabled) {
//        // TODO: Handle permissions on Marshmallow
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED) map.setMyLocationEnabled(enabled);
//    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void setHint(CharSequence hint) {
        TextView hintView = (TextView) findViewById(R.id.hint_textview);
        if (hint == null) {
            hintView.setVisibility(View.GONE);
        } else {
            hintView.setVisibility(View.VISIBLE);
            hintView.setText(hint);
        }
    }

    @Override
    public void setDistance(String distance) {
        TextView distanceView = (TextView) findViewById(R.id.distance_textview);
        if (distance == null) {
            distanceView.setVisibility(View.GONE);
        } else {
            distanceView.setVisibility(View.VISIBLE);
            distanceView.setText(distance);
        }
    }

    @Override
    public GoogleMap getMap() {
        return map;
    }

    @Override
    public void addMarkerForTask(int taskIndex, Task task, int iconResourceId, boolean showText) {
        if (map != null) {

            View mapMarkerView = getLayoutInflater().inflate(R.layout.map_marker, null);
            ImageView iconView = (ImageView) mapMarkerView.findViewById(R.id.icon);
            iconView.setImageResource(iconResourceId);

            IconGenerator iconGenerator = new IconGenerator(this);
            iconGenerator.setBackground(null);
            iconGenerator.setContentView(mapMarkerView);
            Bitmap mapMarkerBitmap;
            if (showText) {
                String iconText = Integer.toString(taskIndex+1);
                mapMarkerBitmap = iconGenerator.makeIcon(iconText);
            } else {
                mapMarkerBitmap = iconGenerator.makeIcon();
            }

            LatLng position = new LatLng(task.getLatitude(), task.getLongitude());
            MarkerOptions options = new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(mapMarkerBitmap));
            Marker marker = map.addMarker(options);
            markers.add(marker);
            boundsBuilder.include(marker.getPosition());
            markerIdsAndTaskIds.put(marker.getId(), task.getId());
        }
    }

    @Override
    public void zoomMapToMarkers() {
        if (map != null) {
            if (markers.size() == 1) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15.5f));
            } else if (markers.size() > 1) {
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                map.moveCamera(newLatLngBounds(boundsBuilder.build(), padding));
            } else {
                Log.w(TAG, "Could not zoom the map since no markers have been added");
            }
        }
    }

    @Override
    public void showMessage(String message, int duration) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog(DialogFragment dialogFragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(dialogFragment, tag);
        transaction.commitAllowingStateLoss();
    }

    // CLICK LISTENERS -------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Cheat button click
        // TODO: Delete before deployment
        if (item.getItemId() == R.id.action_cheat) {
            Task currentTask = presenter.getCurrentTask();
            if (currentTask != null) {
//                int resourceId = presenter.getCurrentTaskIndex() == 0 ?
//                        R.drawable.ic_location_start_48dp : R.drawable.ic_location_48dp;
                int resourceId = R.drawable.ic_location_48dp;
                presenter.setStartLocationIsFound(true);
                presenter.setCurrentLocationIsFound(true);
                addMarkerForTask(presenter.getCurrentTaskIndex(), currentTask, resourceId, true);
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(600);
                presenter.stopLocationUpdates();
                setDistance(null);
                setHint(null);
            }
        }

        // Close button click
        else {
            presenter.onCloseButtonClicked();
            presenter.setLocationServiceShouldStart(false);
        }

        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int clickedTaskId = markerIdsAndTaskIds.get(marker.getId());
        presenter.onMarkerClick(clickedTaskId);
        return true;
    }

    @Override
    public void onPositiveButtonClick(String dialogTag) {
        presenter.onPositiveButtonClick(dialogTag);
    }

    @Override
    public void onBackPressed() {
        presenter.onCloseButtonClicked();
        presenter.setLocationServiceShouldStart(false);
    }

    // MAP READY LISTENER ----------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        boundsBuilder = new LatLngBounds.Builder();
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(this);
//        setMyLocationEnabled(true);
        presenter.loadTasks();
    }
}
