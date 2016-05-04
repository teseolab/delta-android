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
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.MissionPresenter;
import no.ntnu.mikaelr.delta.util.*;
import no.ntnu.mikaelr.delta.view.signature.MissionView;
import no.ntnu.mikaelr.delta.view.TaskActivity;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MissionPresenterImpl implements MissionPresenter, ProjectInteractorImpl.OnFinishedLoadingTasksListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ProjectInteractorImpl.OnPostFinishedMission {

    private MissionView view;
    private AppCompatActivity context;
    private ProjectInteractor interactor;
    private Project project;
    private PhraseGenerator phraseGenerator;

    private Intent serviceIntent;

    private List<Task> loadedTasks;

    private int currentTaskIndex = 0;

    private GoogleApiClient googleApiClient;

    private boolean tasksAreLoaded = false;

    private boolean missionIsCompleted = false;

    static final int TASK_REQUEST = 1;

    public MissionPresenterImpl(MissionView view) {
        this.view = view;
        context = (AppCompatActivity) view;
        serviceIntent = new Intent(context, LocationService.class);
        interactor = new ProjectInteractorImpl();
        project = getProjectFromIntent();
        phraseGenerator = new PhraseGenerator();

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
        Task currentTask = loadedTasks.get(this.currentTaskIndex);
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
        if (currentTaskIndex == -1) {
            return null;
        }
        return loadedTasks.get(currentTaskIndex);
    }

    @Override
    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    @Override
    public void startLocationService() {
        serviceIntent.putExtra("currentTaskLatitude", getCurrentTask().getLatitude());
        serviceIntent.putExtra("currentTaskLongitude", getCurrentTask().getLongitude());
        context.startService(serviceIntent);
    }

    @Override
    public void stopLocationService() {
        context.stopService(serviceIntent);
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
        if (!missionIsCompleted) {
            // TODO: Check permissions on Marshmallow
            int permissionCheck = ContextCompat.checkSelfPermission((Context) view, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(1000);
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
    public void onMarkerClick(int clickedTaskId) {
        if (currentTaskIndex != -1) {
            if (clickedTaskId == getCurrentTask().getId()) {
                goToTask();
            }
        }
    }

    private void taskWasCancelled() {}

    private void taskWasFinished() {

        missionIsCompleted = currentTaskIndex == loadedTasks.size() - 1;

        if (missionIsCompleted) {
            currentTaskIndex = -1;
            interactor.postFinishedMission(project.getId(), this);
            SharedPrefsUtil.getInstance().saveMissionCompletionStatus(project.getId(), Constants.YES);

            context.setResult(Activity.RESULT_OK);
            context.finish();

//            view.showDialog("Gratulerer!", "Du har fullført dette oppdraget. Du kan nå poste forslag og diskutere andres forslag.");
        } else {
            view.addMarkerForTask(currentTaskIndex, loadedTasks.get(currentTaskIndex));
            currentTaskIndex++;
            String title;
            if (currentTaskIndex == 1) {
                title = "Første oppgave ...";
            } else {
                title = phraseGenerator.encouragement()+"!";
            }
            String hint = loadedTasks.get(currentTaskIndex).getHint();
            view.showDialog(title, hint);
            view.setHint(hint);

        }
    }

    // Listeners -------------------------------------------------------------------------------------------------------

    private void addMarkers() {
        if (currentTaskIndex == 0) {
            view.addMarkerForTask(currentTaskIndex, loadedTasks.get(0));
        } else {
            for (int i = 0; i < currentTaskIndex -1; i++) {
                view.addMarkerForTask(currentTaskIndex, loadedTasks.get(i));
            }
        }
    }

    private Task getDefaultFirstTask() {
        Task defaultFirstTask = new Task();
        defaultFirstTask.setId(project.getId());
        defaultFirstTask.setLatitude(project.getLatitude());
        defaultFirstTask.setLongitude(project.getLongitude());
        defaultFirstTask.setTaskType(TaskType.FIRST_TASK);
        defaultFirstTask.setHint("Gå til det markerte punktet på kartet og trykk på det for å starte oppdraget.");
        ArrayList<String> description = new ArrayList<String>(1);
        description.add("Yey! Du er nå klar for å starte oppdraget. Følg beskrivelsen på neste side for å komme i gang.");
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
            view.addMarkerForTask(currentTaskIndex, getCurrentTask());
        }

    }

    private boolean userHasFoundTaskLocation(float distanceToTaskLocation) {
        float radius = 30f;
        return distanceToTaskLocation <= radius;
    }

    private void goToTask() {
        Activity context = (Activity) view;
        Intent intent = new Intent(context, TaskActivity.class);
        intent.putExtra("task", loadedTasks.get(currentTaskIndex));
        intent.putExtra("taskIndex", currentTaskIndex);
        intent.putExtra("projectId", project.getId());
        context.startActivityForResult(intent, TASK_REQUEST);
    }

    // ASYNC TASK LISTENERS --------------------------------------------------------------------------------------------

    @Override
    public void onFinishedLoadingTasks(JSONArray jsonArray) {
        List<Task> tasks = JsonFormatter.formatTasks(jsonArray);
        tasks.add(0, getDefaultFirstTask());

        loadedTasks = tasks;

        addMarkers();

        view.setMapLocationToMarkers();
        view.setHint(tasks.get(currentTaskIndex).getHint());

        tasksAreLoaded = true;

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    @Override
    public void onPostFinishedMissionSuccess() {
        // TODO: ?
    }

    @Override
    public void onPostFinishedMissionError(int errorCode) {
        // TODO: ?
    }
}
