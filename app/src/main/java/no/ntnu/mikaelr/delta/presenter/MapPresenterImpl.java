package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MapPresenter;
import no.ntnu.mikaelr.delta.view.signature.MapFragView;
import no.ntnu.mikaelr.delta.view.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

public class MapPresenterImpl implements MapPresenter {

    private MapFragView view;
    private List<Project> projects;

//    private ProjectInteractor projectInteractor;

    public static final int PROJECT_DIALOG = 1;

    public MapPresenterImpl(MapFragView view) {
        this.view = view;
//        this.projectInteractor = new ProjectInteractorImpl();
    }

    @Override
    public void onMarkerClick(int clickedProjectId) {

        Project project = projects.get(clickedProjectId - 1);
        view.showProjectCard(project.getName(), project.getId());

//        Fragment context = (Fragment) view;
//        DialogFragment dialogFrag = ProjectDialog.newInstance(projects.get(clickedProjectId-1));
//        dialogFrag.setTargetFragment(context, PROJECT_DIALOG);
//        dialogFrag.show(context.getFragmentManager().beginTransaction(), "dialog");
    }

//    @Override
//    public void loadProjects() {
//        projectInteractor.getProjects(this);
//    }

    @Override
    public void goToProjectPage(int projectIndex) {
        Project project = projects.get(projectIndex -1);
        Fragment context = (Fragment) view;
        Intent intent = new Intent(context.getActivity(), ProjectActivity.class);
        intent.putExtra("id", project.getId());
        intent.putExtra("name", project.getName());
        intent.putExtra("description", project.getDescription());
        intent.putExtra("imageUri", project.getImageUri());
        intent.putExtra("latitude", project.getLatitude());
        intent.putExtra("longitude", project.getLongitude());
        context.getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROJECT_DIALOG) {
            if (resultCode == Activity.RESULT_OK) {
                int projectId = data.getIntExtra("projectId", -1);
                goToProjectPage(projectId);
            }
        }
    }

    @Override
    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    @Override
    public List<Project> getProjects() {
        return projects;
    }

//    @Override
//    public void onFinishedLoadingProjects(JSONArray jsonResult) {
//        if (jsonResult.length() != 0) {
//            List<Project> projects = JsonFormatter.formatProjects(jsonResult);
//            this.projects = projects;
//            for (Project project : projects) {
//                view.addMarkerForProject(project);
//            }
//            view.setMapLocationToMarkers();
//            view.initializeProjectList(projects);
//        }
//    }

}
