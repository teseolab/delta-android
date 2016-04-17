package no.ntnu.mikaelr.delta.view.signature;

import android.os.Bundle;
import no.ntnu.mikaelr.delta.model.Project;

public interface MapFragView {

    void addMarkerForProject(Project project);
    void setMapLocationToMarkers();

}
