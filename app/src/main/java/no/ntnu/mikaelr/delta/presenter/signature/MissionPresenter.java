package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;

public interface MissionPresenter {

    // TODO: DELETE AFTER TEST
    void setStartLocationIsFound(boolean startLocationIsFound);

    void loadTasks();
    Project getProject();
    Task getCurrentTask();

    void connectApiClient();
    void disconnectApiClient();
    void stopLocationUpdates();

    boolean googleApiClientIsConnected();

    void startLocationUpdates();

    void onActivityResult(int requestCode, Intent data);
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onCloseButtonClicked();
    void onPositiveButtonClick(String dialogTag);
    void onMarkerClick(int clickedTaskId);

    int getCurrentTaskIndex();

    void onPause();
    void stopLocationService();

    void setLocationServiceShouldStart(boolean shouldStart);

    void setCurrentLocationIsFound(boolean isFound);

    boolean startLocationIsFound();
}
