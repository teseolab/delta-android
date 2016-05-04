package no.ntnu.mikaelr.delta.view;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import no.ntnu.mikaelr.delta.fragment.SimpleDialog;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.MissionPresenter;
import no.ntnu.mikaelr.delta.presenter.MissionPresenterImpl;
import no.ntnu.mikaelr.delta.util.LocationService;
import no.ntnu.mikaelr.delta.util.PhraseGenerator;
import no.ntnu.mikaelr.delta.view.signature.MissionView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;

public class MissionActivity extends AppCompatActivity implements MissionView, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private MissionPresenter presenter;

    private GoogleMap map;
    private List<Marker> markers = new ArrayList<Marker>();
    private LatLngBounds.Builder boundsBuilder;

    private HashMap<String, Integer> markerIdsAndTaskIds = new HashMap<String, Integer>();

    private boolean locationServiceShouldStart = true;

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        this.presenter = new MissionPresenterImpl(this);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, presenter.getProject().getName()+" oppdrag");
        presenter.connectApiClient();
        initializeMap();
        showDialog("For å komme i gang", "Gå til det markerte punktet på kartet og trykk på det for å starte oppdraget.");
    }

    @Override
    protected void onDestroy() {
        if (isMyServiceRunning(LocationService.class)) {
            presenter.stopLocationService();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_mission, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (presenter.googleApiClientIsConnected()) {
            presenter.stopLocationUpdates();
            presenter.disconnectApiClient();
        }

        if (locationServiceShouldStart) {
            presenter.startLocationService();
            locationServiceShouldStart = true;
        }

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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        presenter.disconnectApiClient();
        super.onStop();
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mission_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cheat) {
            Task currentTask = presenter.getCurrentTask();
            if (currentTask != null) {
                addMarkerForTask(presenter.getCurrentTaskIndex(), currentTask);
            }
        } else {
            locationServiceShouldStart = false;
            finish();
        }
        return true;
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public void setHint(CharSequence hint) {
        TextView hintView = (TextView) findViewById(R.id.hint_textview);
        hintView.setText(hint);
    }

    @Override
    public void setDistance(String distance) {
        TextView distanceView = (TextView) findViewById(R.id.distance_textview);
        distanceView.setText(distance);
    }

    @Override
    public void addMarkerForTask(int taskIndex, Task task) {
        if (map != null) {

            View mapMarkerView = getLayoutInflater().inflate(R.layout.map_marker, null);
            ImageView iconView = (ImageView) mapMarkerView.findViewById(R.id.icon);
            String iconText = "";

            if (taskIndex == 0) {
                iconView.setImageResource(R.drawable.ic_location_start_48dp);
            } else {
                iconView.setImageResource(R.drawable.ic_location_48dp);
                iconText = Integer.toString(taskIndex);
            }

            IconGenerator iconGenerator = new IconGenerator(this);
            iconGenerator.setBackground(null);
            iconGenerator.setContentView(mapMarkerView);
            Bitmap mapMarkerBitmap = iconGenerator.makeIcon(iconText);

            LatLng position = new LatLng(task.getLatitude(), task.getLongitude());
            MarkerOptions options = new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(mapMarkerBitmap));
            Marker marker = map.addMarker(options);
            markers.add(marker);
            boundsBuilder.include(marker.getPosition());
            markerIdsAndTaskIds.put(marker.getId(), task.getId());
        }
    }

    @Override
    public void setMapLocationToMarkers() {
        if (map != null) {
            if (markers.size() == 1) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15.5f));
            } else {
                map.moveCamera(newLatLngBounds(boundsBuilder.build(), 550));
            }
        }
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {
        // TODO: Handle permissions on Marshmallow
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) map.setMyLocationEnabled(enabled);
    }

    @Override
    public void showMessage(String message, int duration) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(String title, String hint) {
        SimpleDialog dialog = SimpleDialog.newInstance(title, hint);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(dialog, null);
        transaction.commitAllowingStateLoss();
    }


    // Listeners -------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(this);
        boundsBuilder = new LatLngBounds.Builder();
        presenter.loadTasks();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        locationServiceShouldStart = false;
        int clickedTaskId = markerIdsAndTaskIds.get(marker.getId());
        presenter.onMarkerClick(clickedTaskId);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationServiceShouldStart = true; // TODO: This assumes that the only intent sending results here is TaskActivity
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        locationServiceShouldStart = false;
        super.onBackPressed();
    }
}
