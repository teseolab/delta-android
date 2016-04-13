package no.ntnu.mikaelr.delta.presenter;

import android.content.Intent;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;

import java.util.List;

public interface MissionPresenter {

    void loadTasks();
    Project getProject();
    Task getCurrentTask();

    void connectApiClient();
    void disconnectApiClient();
    void stopLocationUpdates();

    boolean googleApiClientIsConnected();

    void startLocationUpdates();

    void onActivityResult(int requestCode, Intent data);
}
