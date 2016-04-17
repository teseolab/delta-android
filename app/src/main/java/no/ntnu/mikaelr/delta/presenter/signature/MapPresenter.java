package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;

import java.util.List;

public interface MapPresenter {

    void onMarkerClick(int clickedProjectId);

    void loadProjects();

    void goToProjectPage(int projectId);

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
