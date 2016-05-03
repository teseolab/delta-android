package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import no.ntnu.mikaelr.delta.fragment.ProjectDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MapPresenter;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.signature.MapFragView;
import no.ntnu.mikaelr.delta.view.ProjectActivity;
import org.json.JSONArray;

import java.util.List;

public class MapPresenterImpl implements MapPresenter, ProjectInteractorImpl.OnFinishedLoadingProjectsListener {

    private MapFragView view;
    List<Project> loadedProjects;

    private ProjectInteractor projectInteractor;

    public static final int PROJECT_DIALOG = 1;

    public MapPresenterImpl(MapFragView view) {
        this.view = view;
        this.projectInteractor = new ProjectInteractorImpl();
    }

    @Override
    public void onMarkerClick(int clickedProjectId) {
        Fragment context = (Fragment) view;
        DialogFragment dialogFrag = ProjectDialog.newInstance(loadedProjects.get(clickedProjectId-1));
        dialogFrag.setTargetFragment(context, PROJECT_DIALOG);
        dialogFrag.show(context.getFragmentManager().beginTransaction(), "dialog");
    }

    @Override
    public void loadProjects() {
        projectInteractor.getProjects(this);
    }

    @Override
    public void goToProjectPage(int projectId) {
        Project project = loadedProjects.get(projectId-1);
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Fragment context = (Fragment) view;
                FragmentTransaction ft = context.getFragmentManager().beginTransaction();
                Fragment prev = context.getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public void onFinishedLoadingProjects(JSONArray jsonResult) {
        if (jsonResult.length() != 0) {
            List<Project> projects = JsonFormatter.formatProjects(jsonResult);
            this.loadedProjects = projects;
            for (Project project : projects) {
                view.addMarkerForProject(project);
            }
            view.setMapLocationToMarkers();
        }
    }

}
