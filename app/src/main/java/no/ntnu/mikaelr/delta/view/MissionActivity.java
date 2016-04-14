package no.ntnu.mikaelr.delta.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.MissionPresenter;
import no.ntnu.mikaelr.delta.presenter.MissionPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.TestMissionPresenterImpl;

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

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        this.presenter = new MissionPresenterImpl(this);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, presenter.getProject().getName()+" oppdrag");
        presenter.connectApiClient();
        initializeMap();
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
        super.onPause();
    }

    @Override
    public void onResume() {
        if (presenter.googleApiClientIsConnected()) {
            presenter.startLocationUpdates();
        } else {
            presenter.connectApiClient();
        }
        super.onResume();
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
                addMarkerForTask(currentTask);
            }
        } else {
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
    public void addMarkerForTask(Task task) {
        if (map != null) {
            LatLng position = new LatLng(task.getLatitude(), task.getLongitude());
            MarkerOptions options = new MarkerOptions().position(position);
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
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) map.setMyLocationEnabled(enabled);
    }

    @Override
    public void showMessage(String message, int duration) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Listeners -------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        boundsBuilder = new LatLngBounds.Builder();
        presenter.loadTasks();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int clickedTaskId = markerIdsAndTaskIds.get(marker.getId());
        presenter.onMarkerClick(clickedTaskId);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode, data);
        }
    }

}
