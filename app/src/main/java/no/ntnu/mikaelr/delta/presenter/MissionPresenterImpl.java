package no.ntnu.mikaelr.delta.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.MissionPresenter;
import no.ntnu.mikaelr.delta.util.*;
import no.ntnu.mikaelr.delta.view.signature.MissionView;
import no.ntnu.mikaelr.delta.view.TaskActivity;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.List;

public class MissionPresenterImpl implements MissionPresenter, ProjectInteractorImpl.OnGetTasksListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ProjectInteractorImpl.OnPostFinishedMission {

    private MissionView view;
    private AppCompatActivity context;
    private ProjectInteractor interactor;
    private Project project;
    private GoogleApiClient googleApiClient;
    private PhraseGenerator phraseGenerator;
    private Intent serviceIntent;

    private List<Task> loadedTasks;
    private int currentTaskIndex = 0;

    private boolean tasksAreLoaded = false;
    private boolean missionIsCompleted = false;
    private boolean locationServiceShouldStart = true;

    static final int TASK_REQUEST = 1;
    static final int REQUEST_ACCESS_FINE_LOCATION = 2;
    static final String CLOSE_MISSION_DIALOG_TAG = "closeMissionDialog";
    private boolean taskWasCancelled = false;
    private boolean locationAccessWasDenied = false;

    private boolean startLocationIsFound = false;
    private boolean currentLocationIsFound = false;

    @Override
    public void setStartLocationIsFound(boolean startLocationIsFound) {
        this.startLocationIsFound = startLocationIsFound;
    }


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
    public void onPause() {

        if (googleApiClientIsConnected()) {
            stopLocationUpdates();
            disconnectApiClient();
        }

        // TODO: 08.05.2016
        // Today when opening MissionActivity, onPause was called immediately.
        // This caused the app to crash, since the tasks had not been loaded.
        // The first check here should avoid the crash, but I don't what why onPause was called.
        // Could also have been "fixed" by setting default value of locationServiceShouldStart to false.
        if (getCurrentTask() != null) {
            if (locationServiceShouldStart) {
                serviceIntent.putExtra("currentTaskLatitude", getCurrentTask().getLatitude());
                serviceIntent.putExtra("currentTaskLongitude", getCurrentTask().getLongitude());
                context.startService(serviceIntent);
            }
//            else {
//                if (currentLocationIsFound) {
//                    SharedPrefsUtil.getInstance().setLocationFoundStatus(project.getId(), loadedTasks.get(currentTaskIndex).getId(), Constants.YES);
//                }
//            }
        }
    }

    @Override
    public void stopLocationService() {
        context.stopService(serviceIntent);
    }

    @Override
    public void setLocationServiceShouldStart(boolean shouldStart) {
        locationServiceShouldStart = shouldStart;
    }

    @Override
    public void setCurrentLocationIsFound(boolean isFound) {
        currentLocationIsFound = isFound;
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
        if (!missionIsCompleted && !taskWasCancelled) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else if (!googleApiClientIsConnected()) {
                connectApiClient();
            } else  {
                view.getMap().setMyLocationEnabled(true);
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationAccessWasDenied = false;
                    startLocationUpdates();
                } else {
                    locationAccessWasDenied = true;
                    view.setDistance("OBS!");
                    view.setHint("Appen trenger din posisjon for å starte et oppdrag.");
                }
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
    public void onCloseButtonClicked() {
        if (missionIsCompleted) {
            locationServiceShouldStart = false;
            if (loadedTasks.size() > currentTaskIndex) {
                currentLocationIsFound = true;
            }
        }
        context.finish();
    }

    @Override
    public void onPositiveButtonClick(String dialogTag) {
        if (dialogTag != null && dialogTag.equals(CLOSE_MISSION_DIALOG_TAG)) {
            locationServiceShouldStart = false;
            context.finish();
        }
    }

    @Override
    public void onMarkerClick(int clickedTaskId) {

        boolean missionIsNotComplete = currentTaskIndex != -1;

        if (missionIsNotComplete) {
            if (startLocationIsFound) {
                boolean userClickedCurrentTaskMarker = clickedTaskId == getCurrentTask().getId();
                if (userClickedCurrentTaskMarker) {
                    setLocationServiceShouldStart(false);
                    goToTask();
                }
            } else {
                String title = "For å starte oppdraget";
                String message = "Du må dra til dette punktet før du kan starte. Det vises et kompass i markøren når du er fremme.";
                CustomDialog dialog = CustomDialog.newInstance(title, message, "Ok", null, R.drawable.explore);
                view.showDialog(dialog, null);
            }
        }
    }

    private void taskWasCancelled() {
        taskWasCancelled = true;
    }

    private void taskWasFinished() {

        taskWasCancelled = false;
        missionIsCompleted = currentTaskIndex == loadedTasks.size() - 1;

        if (currentTaskIndex == 0) {
            SharedPrefsUtil.getInstance().setFirstTaskFinishedStatus(project.getId(), Constants.YES);
        }

        if (missionIsCompleted) {
            currentTaskIndex = -1;
            interactor.postFinishedMission(project.getId(), this);
            String username = SharedPrefsUtil.getInstance().getUsername();
            SharedPrefsUtil.getInstance().setMissionCompletionStatus(project.getId(), username, Constants.YES);

            context.setResult(Activity.RESULT_OK);
            context.finish();

        } else {
            currentTaskIndex++;
            currentLocationIsFound = false;

            String title = currentTaskIndex == 1 ? "Første oppgave" : phraseGenerator.encouragement()+"!";
            String hint = loadedTasks.get(currentTaskIndex).getHint();

            view.showDialog(CustomDialog.newInstance(title, hint, "Ok", null, 0), null);
            view.setDistance("Beregner distanse...");
            view.setHint(hint);

            setLocationServiceShouldStart(true);
        }
    }

    // Listeners -------------------------------------------------------------------------------------------------------

    private void addFinishedMarkers() {
        if (startLocationIsFound) {
            view.addMarkerForTask(0, loadedTasks.get(0), R.drawable.ic_location_start_48dp);
        } else {
            view.addMarkerForTask(0, loadedTasks.get(0), R.drawable.ic_location_48dp);
        }
        for (int i = 1; i < currentTaskIndex; i++) {
            view.addMarkerForTask(i, loadedTasks.get(i), R.drawable.ic_location_48dp);
        }
    }

    private Task getDefaultFirstTask() {
        Task defaultFirstTask = new Task();
        defaultFirstTask.setId(0);
        defaultFirstTask.setLatitude(project.getLatitude());
        defaultFirstTask.setLongitude(project.getLongitude());
        defaultFirstTask.setTaskType(TaskType.FIRST_TASK);
        defaultFirstTask.setHint("Gå til det markerte punktet på kartet og trykk på det for å starte oppdraget.");
        String description = "Yey! Du er nå klar for å starte oppdraget. Følg beskrivelsen på neste side for å komme i gang.";
        defaultFirstTask.setDescription(description);
        return defaultFirstTask;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (loadedTasks != null) {
            // TODO: 11.05.2016
            // If location permission was denied, onConnect is called again, which again requests the permission
            // causing the permission dialog to appear over and over again.
            // This test avoids the problem.
            if (!locationAccessWasDenied) {
                startLocationUpdates();
            }
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
            startLocationIsFound = true;
            currentLocationIsFound = true;
            stopLocationUpdates();
            int iconResourceId = currentTaskIndex == 0 ? R.drawable.ic_location_start_48dp : R.drawable.ic_location_48dp;
            view.addMarkerForTask(currentTaskIndex, getCurrentTask(), iconResourceId);
            view.setDistance(null);
            view.setHint(null);
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(600);
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
    public void onGetTasksSuccess(JSONArray jsonArray) {

        List<Task> tasks = JsonFormatter.formatTasks(jsonArray);
        tasks.add(0, getDefaultFirstTask());
        loadedTasks = tasks;

        // Check SharedPreferences if the start location has already been completed as this is not stored on the server
        startLocationIsFound = SharedPrefsUtil.getInstance().getFirstTaskFinishedStatus(project.getId()).equals(Constants.YES);
        if (startLocationIsFound) {
            currentTaskIndex = 1;
        }

        initializeCurrentTaskIndex(tasks);
        addFinishedMarkers();
        view.zoomMapToMarkers();
        view.setHint(tasks.get(currentTaskIndex).getHint());

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private void initializeCurrentTaskIndex(List<Task> tasks) {
        int i = 1;
        while (tasks.get(i).isFinished()) {
            i = i + 1;
            currentTaskIndex = i;
        }
    }

    @Override
    public void onGetTasksError(int errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.showMessage(ErrorMessage.COULD_NOT_LOAD_TASKS, Toast.LENGTH_LONG);
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
