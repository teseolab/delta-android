package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MapPresenter;
import no.ntnu.mikaelr.delta.presenter.MapPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.MainView;
import no.ntnu.mikaelr.delta.view.signature.MapFragView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;

public class MapFragment extends Fragment implements MapFragView, OnMapReadyCallback {

    private MapPresenter presenter;

    private MapView mapView;
    private GoogleMap map;
    private View projectCard;
    private Marker selectedMarker;

    private View view;

    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    private HashMap<String, Integer> markerIdsAndProjectIds = new HashMap<String, Integer>();

    public static MapFragment newInstance(List<Project> projects) {
        Bundle args = new Bundle();
        args.putSerializable("projects", (ArrayList) projects);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);

            presenter = new MapPresenterImpl(this);
            presenter.setProjects((ArrayList<Project>) getArguments().getSerializable("projects"));

            mapView = (MapView) view.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

            projectCard = view.findViewById(R.id.project_card);
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
        mapView.setVisibility(View.INVISIBLE);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_idea_48dp));
                }
                projectCard.setVisibility(View.GONE);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_idea_48dp));
                }

                selectedMarker = marker;
                selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_idea_selected_48dp));

                int clickedProjectId = markerIdsAndProjectIds.get(marker.getId());
                presenter.onMarkerClick(clickedProjectId);
                return false;
            }
        });
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                List<Project> projects = presenter.getProjects();
                if (projects != null) {
                    for (Project project : presenter.getProjects()) {
                        addMarkerForProject(project);
                    }
                } else {
                    Log.w("MapFragment", "Could not add markers to map since presenter.getProjects() returns null");
                }
                setMapLocationToMarkers();
            }
        });
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.target.latitude > 0 && cameraPosition.target.longitude > 0) {

//                    View mapWrapper = view.findViewById(R.id.map_wrapper);
//                    mapWrapper.setVisibility(View.VISIBLE);

                    mapView.setVisibility(View.VISIBLE);

                    MainView mainView = (MainView) getActivity();
                    mainView.removeProjectListFragment();
                    mainView.setMapIsLoading(false);
                    mainView.supportInvalidateOptionsMenu();
                }
            }
        });
    }


    @Override
    public void addMarkerForProject(Project project) {
        if (map != null) {
            LatLng position = new LatLng(project.getLatitude(), project.getLongitude());
            MarkerOptions options = new MarkerOptions().position(position).title(project.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_idea_48dp));
            Marker marker = map.addMarker(options);
            boundsBuilder.include(marker.getPosition());
            markerIdsAndProjectIds.put(marker.getId(), project.getId());
        }
    }

    @Override
    public void setMapLocationToMarkers() {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
        if (map != null) map.moveCamera(newLatLngBounds(boundsBuilder.build(), padding));
//        if (map != null) map.animateCamera(newLatLngBounds(boundsBuilder.build(), padding));
    }

    @Override
    public void showProjectCard(String name, Integer id) {
        TextView projectName = (TextView) projectCard.findViewById(R.id.project_name);
        projectName.setText(name);
        final Button readMoreButton = (Button) projectCard.findViewById(R.id.button);
        readMoreButton.setTag(id);
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.goToProjectPage((Integer) readMoreButton.getTag());
            }
        });
        projectCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

}
