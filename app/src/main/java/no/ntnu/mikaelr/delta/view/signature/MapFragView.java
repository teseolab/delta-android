package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.Project;

import java.util.List;

public interface MapFragView {

    void addMarkerForProject(Project project);
    void setMapLocationToMarkers();

    void showProjectCard(String name, Integer id);

//    void initializeProjectList(List<Project> projects);
}
