package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;
import no.ntnu.mikaelr.delta.model.Project;

import java.util.ArrayList;
import java.util.List;

public interface MapPresenter {

    void onMarkerClick(int clickedProjectId);

//    void loadProjects();

    void goToProjectPage(int projectIndex);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void setProjects(ArrayList<Project> projects);

    List<Project> getProjects();
}
