package no.ntnu.mikaelr.delta.view.signature;

import android.support.v4.app.DialogFragment;
import no.ntnu.mikaelr.delta.model.Task;

public interface MissionView {
    void addMarkerForTask(int taskIndex, Task task, int iconResourceId);
    void setHint(CharSequence hint);
    void setDistance(String distance);
    void zoomMapToMarkers();
    void showMessage(String message, int duration);
    void showDialog(DialogFragment dialogFragment, String tag);
    void finish();
}
