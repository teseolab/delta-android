package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import no.ntnu.mikaelr.delta.fragment.ProjectDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.MainView;
import no.ntnu.mikaelr.delta.view.ProjectActivity;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    private ProjectInteractor projectInteractor;

    //private List<Project> loadedProjects;

    public MainPresenterImpl(MainView view) {
        this.view = view;
        this.projectInteractor = new ProjectInteractorImpl();
    }

//    @Override
//    public void onMarkerClick(int clickedProjectId) {
//        AppCompatActivity context = (AppCompatActivity) view;
//        FragmentManager fm = context.getSupportFragmentManager();
//        ProjectDialog projectDialog = ProjectDialog.newInstance(loadedProjects.get(clickedProjectId));
//        projectDialog.show(fm, "fragment_project_dialog");
//    }

    @Override
    public List<String> getDrawerMenuItems() {
        return new ArrayList<String>(Arrays.asList("Prosjekter", "Profil", "Toppliste", "Min aktivitet"));
    }

//    @Override
//    public void loadProjects() {
//        projectInteractor.getProjects(this);
//    }

//    @Override
//    public void goToProjectPage(int projectId) {
//        Project project = loadedProjects.get(projectId);
//        Activity context = (Activity) view;
//        Intent intent = new Intent(context, ProjectActivity.class);
//        intent.putExtra("id", project.getId());
//        intent.putExtra("name", project.getName());
//        intent.putExtra("description", project.getDescription());
//        intent.putExtra("latitude", project.getLatitude());
//        intent.putExtra("longitude", project.getLongitude());
//        context.startActivity(intent);
//    }

//    @Override
//    public void onFinishedLoadingProjects(JSONArray jsonResult) {
//        if (jsonResult.length() != 0) {
//            List<Project> projects = JsonFormatter.formatProjects(jsonResult);
//            this.loadedProjects = projects;
//            for (Project project : projects) {
//                view.addMarkerForProject(project);
//            }
//            view.setMapLocationToMarkers();
//        }
//    }

}
