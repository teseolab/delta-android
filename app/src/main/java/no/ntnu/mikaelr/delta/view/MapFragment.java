package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MapPresenter;
import no.ntnu.mikaelr.delta.presenter.MapPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.MapFragView;

import java.util.HashMap;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;

public class MapFragment extends Fragment implements MapFragView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLoadedCallback {

    private MapPresenter presenter;

    private MapView mapView;
    private GoogleMap map;

    View view;

    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    private HashMap<String, Integer> markerIdsAndProjectIds = new HashMap<String, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);

            presenter = new MapPresenterImpl(this);

            mapView = (MapView) view.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        if (view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapLoadedCallback(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int clickedProjectId = markerIdsAndProjectIds.get(marker.getId());
        presenter.onMarkerClick(clickedProjectId);
        return true;
    }

    @Override
    public void onMapLoaded() {
        presenter.loadProjects();
    }

    @Override
    public void addMarkerForProject(Project project) {
        if (map != null) {
            LatLng position = new LatLng(project.getLatitude(), project.getLongitude());
            MarkerOptions options = new MarkerOptions().position(position).title(project.getName());
            Marker marker = map.addMarker(options);
            boundsBuilder.include(marker.getPosition());
            markerIdsAndProjectIds.put(marker.getId(), project.getId());
        }
    }

    @Override
    public void setMapLocationToMarkers() {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
//        if (map != null) map.moveCamera(newLatLngBounds(boundsBuilder.build(), padding));
        if (map != null) map.animateCamera(newLatLngBounds(boundsBuilder.build(), padding));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

}
