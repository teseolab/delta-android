package no.ntnu.mikaelr.delta.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.MissionView;
import no.ntnu.mikaelr.delta.view.TaskActivity;
import no.ntnu.mikaelr.delta.view.TestTask1Activity;
import no.ntnu.mikaelr.delta.view.TestTask2Activity;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMissionPresenterImpl implements TestMissionPresenter, ProjectInteractorImpl.OnFinishedLoadingTasksListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MissionView view;
    private Activity context;
    private ProjectInteractor interactor;
    private Project project;

    private List<Task> loadedTasks;

    private int currentTaskListId = 0;

    private GoogleApiClient googleApiClient;

    private boolean tasksAreLoaded = false;
    private boolean allTasksAreFinished = false; // TODO: Initialize when state is received from server

    static final int TASK_REQUEST = 1;

    public TestMissionPresenterImpl(MissionView view) {
        this.view = view;
        this.context = (Activity) view;
        this.interactor = new ProjectInteractorImpl();
        this.project = getProjectFromIntent();

        initializeGoogleApiClient();
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private Project getProjectFromIntent() {
        Intent intent = ((Activity) view).getIntent();
        Project project = new Project();
        project.setId(intent.getIntExtra("projectId", -1));
        project.setName(intent.getStringExtra("projectName"));
        project.setLatitude(intent.getFloatExtra("latitude", -1));
        project.setLongitude(intent.getFloatExtra("longitude", -1));
        return project;
    }

    private void initializeGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder((Context) view)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private Location getCurrentTaskLocation() {
        Task currentTask = loadedTasks.get(currentTaskListId);
        float taskLatitude = currentTask.getLatitude();
        float taskLongitude = currentTask.getLongitude();
        Location taskLocation = new Location("randomProvider");
        taskLocation.setLatitude(taskLatitude);
        taskLocation.setLongitude(taskLongitude);
        return taskLocation;
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public void loadTasks() {
        interactor.getTasks(project.getId(), this);
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public Task getCurrentTask() {
        if (currentTaskListId == -1) {
            return null;
        }
        Task task = loadedTasks.get(currentTaskListId);
        return task;
    }

    @Override
    public int getCurrentTaskListId() {
        return currentTaskListId;
    }

    @Override
    public void connectApiClient() {
        googleApiClient.connect();
    }

    @Override
    public void disconnectApiClient() {
        googleApiClient.disconnect();
    }

    @Override
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public boolean googleApiClientIsConnected() {
        return googleApiClient.isConnected();
    }

    @Override
    public void startLocationUpdates() {
        if (!allTasksAreFinished) {
            int permissionCheck = ContextCompat.checkSelfPermission((Context) view, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(2000);
                //mLocationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent intent) {
        if (requestCode == TASK_REQUEST) {
            int action = intent.getIntExtra("action", -1);
            if (action == Constants.TASK_FINISHED) {
                taskWasFinished();
            }
            else if (action == Constants.TASK_CANCELLED) {
                taskWasCancelled();
            }
        }
    }

    @Override
    public void onMarkerClick(int taskId) {
        if (taskId == getCurrentTask().getId()) {
            goToTask();
        }
    }

    private void taskWasCancelled() {
        view.showMessage("Oppgave ble avbrutt", Toast.LENGTH_SHORT);
    }

    private void taskWasFinished() {

        if (currentTaskListId == loadedTasks.size()-1) {
            currentTaskListId = -1;
            view.setDistance("Gratulerer!");
            view.setHint("Du har fullført dette oppdraget.");
            allTasksAreFinished = true;
            view.setMyLocationEnabled(false);
        } else {
            //view.addMarkerForTask(loadedTasks.get(currentTaskListId));
            currentTaskListId++;
            view.setHint(loadedTasks.get(currentTaskListId).getHint());
        }
    }

    // Listeners -------------------------------------------------------------------------------------------------------

    @Override
    public void onFinishedLoadingTasks(JSONArray jsonArray) {
        List<Task> tasks = JsonFormatter.formatTasks(jsonArray);
        tasks.add(0, getDefaultFirstTask());

        loadedTasks = tasks;

        addMarkers();

        view.setMapLocationToMarkers();
        view.setHint(tasks.get(currentTaskListId).getHint());


        tasksAreLoaded = true;

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private void addMarkers() {
        if (currentTaskListId == 0) {
            view.addMarkerForTask(loadedTasks.get(0));
        } else {
            for (int i = 0; i < currentTaskListId -1; i++) {
                view.addMarkerForTask(loadedTasks.get(i));
            }
        }
    }

    private Task getDefaultFirstTask() {
        Task defaultFirstTask = new Task();
        defaultFirstTask.setId(project.getId());
        defaultFirstTask.setLatitude(project.getLatitude());
        defaultFirstTask.setLongitude(project.getLongitude());
        defaultFirstTask.setTaskType(Task.TaskType.FIRST_TASK);
        defaultFirstTask.setHint("Gå til det markerte punktet på kartet for å starte oppdraget.");
        ArrayList<String> description = new ArrayList<String>(1);
        description.add("Yey! Du er nå klar for å starte oppdraget. Følg veibeskrivelsen for å finne første stopp.");
        defaultFirstTask.setDescriptions(description);
        return defaultFirstTask;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (tasksAreLoaded) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        float distanceToTaskLocation = location.distanceTo(getCurrentTaskLocation());
        view.setDistance("Neste punkt: " + String.format("%.0f", distanceToTaskLocation) + " m");

        if (userHasFoundTaskLocation(distanceToTaskLocation)) {
            view.addMarkerForTask(loadedTasks.get(currentTaskListId));
            //goToTask();
        }

    }

    private boolean userHasFoundTaskLocation(float distanceToTaskLocation) {
        float radius = 20f;
        return distanceToTaskLocation <= radius;
    }

    private void goToTask() {
//        Activity context = (Activity) view;
//        Intent intent = new Intent(context, TaskActivity.class);
//        intent.putExtra("task", loadedTasks.get(currentTaskListId));
//        context.startActivityForResult(intent, TASK_REQUEST);

        if (currentTaskListId == 0) {
            Activity context = (Activity) view;
            Intent intent = new Intent(context, TestTask1Activity.class);
            intent.putExtra("task", loadedTasks.get(currentTaskListId));
            context.startActivityForResult(intent, TASK_REQUEST);
        }

        else if (currentTaskListId == 1) {
            Activity context = (Activity) view;
            Intent intent = new Intent(context, TestTask2Activity.class);
            intent.putExtra("task", loadedTasks.get(currentTaskListId));
            context.startActivityForResult(intent, TASK_REQUEST);
        }

        if (currentTaskListId == 2) {
            Activity context = (Activity) view;
            Intent intent = new Intent(context, TestTask1Activity.class);
            intent.putExtra("task", loadedTasks.get(currentTaskListId));
            context.startActivityForResult(intent, TASK_REQUEST);
        }
    }
}
