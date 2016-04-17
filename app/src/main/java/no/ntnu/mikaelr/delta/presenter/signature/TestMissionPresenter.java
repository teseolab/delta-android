package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;

public interface TestMissionPresenter {

    void loadTasks();
    Project getProject();
    Task getCurrentTask();
    int getCurrentTaskListId();

    void connectApiClient();
    void disconnectApiClient();
    void stopLocationUpdates();

    boolean googleApiClientIsConnected();

    void startLocationUpdates();

    void onActivityResult(int requestCode, Intent data);

    void onMarkerClick(int taskId);
}
