package no.ntnu.mikaelr.delta.presenter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.ProjectListPresenter;
import no.ntnu.mikaelr.delta.view.ProjectActivity;
import no.ntnu.mikaelr.delta.view.signature.ProjectListView;

public class ProjectListPresenterImpl implements ProjectListPresenter {

    private ProjectListView view;

    public ProjectListPresenterImpl(ProjectListView view) {
        this.view = view;
    }

    @Override
    public void goToProjectPage(Project project) {
        Fragment context = (Fragment) view;
        Intent intent = new Intent(context.getActivity(), ProjectActivity.class);
        intent.putExtra("id", project.getId());
        intent.putExtra("name", project.getName());
        intent.putExtra("description", project.getDescription());
        intent.putExtra("imageUri", project.getImageUri());
        intent.putExtra("latitude", project.getLatitude());
        intent.putExtra("longitude", project.getLongitude());
        intent.putExtra("missionEnabled", project.isMissionEnabled());
        context.getActivity().startActivity(intent);
    }

}
