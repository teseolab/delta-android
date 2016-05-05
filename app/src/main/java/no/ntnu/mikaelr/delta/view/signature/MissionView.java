package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.Task;

public interface MissionView {

    void addMarkerForTask(int taskIndex, Task task, int iconResourceId);
    void setHint(CharSequence hint);
    void setDistance(String distance);
//    void setMyLocationEnabled(boolean enabled);

    void zoomMapToMarkers();
    void showMessage(String message, int duration);

    void showDialog(String title, String hint);

    void finish();

}
