package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.signature.MainView;
import org.json.JSONArray;

import java.util.ArrayList;

public class MainPresenterImpl implements MainPresenter, ProjectInteractorImpl.OnFinishedLoadingProjectsListener {

    private MainView view;
    private ProjectInteractor projectInteractor;
    private ArrayList<Project> projects;

    public MainPresenterImpl(MainView view) {
        this.view = view;
        this.projectInteractor = new ProjectInteractorImpl();
    }

    @Override
    public void loadProjects() {
        projectInteractor.getProjects(this);
    }

    @Override
    public ArrayList<Project> getProjects() {
        return projects;
    }

    @Override
    public void onFinishedLoadingProjects(JSONArray jsonArray) {
        if (jsonArray.length() != 0) {
            this.projects = (ArrayList<Project>) JsonFormatter.formatProjects(jsonArray);
            view.addProjectListFragment(projects);
        }
    }
}
