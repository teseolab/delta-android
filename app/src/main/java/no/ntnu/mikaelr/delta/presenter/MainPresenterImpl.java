package no.ntnu.mikaelr.delta.presenter;

import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.signature.MainView;
import org.json.JSONArray;

import java.util.ArrayList;

public class MainPresenterImpl implements MainPresenter, ProjectInteractorImpl.OnGetProjectsListener {

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
    public void onGetProjectsSuccess(JSONArray jsonArray) {
        if (jsonArray.length() != 0) {
            this.projects = (ArrayList<Project>) JsonFormatter.formatProjects(jsonArray);
            view.addProjectListFragment(projects);
        } else {
            Log.w("MainPresenterImpl", "No projects were returned from the server");
        }
    }

    @Override
    public void onGetProjectsError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_LOAD_PROJECTS, Toast.LENGTH_LONG);
    }
}
