package no.ntnu.mikaelr.delta.view;

import no.ntnu.mikaelr.delta.model.Task;

public interface MissionView {

    void addMarkerForTask(Task task);
    void setHint(CharSequence hint);
    void setDistance(String distance);
    void setMyLocationEnabled(boolean enabled);

    void setMapLocationToMarkers();
    void showMessage(String message, int duration);

}
