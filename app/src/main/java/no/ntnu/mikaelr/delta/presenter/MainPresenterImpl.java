package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.LoginActivity;
import no.ntnu.mikaelr.delta.view.signature.MainView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class MainPresenterImpl implements MainPresenter, ProjectInteractorImpl.OnGetProjectsListener {

    private MainView view;
    private Activity context;
    private ProjectInteractor projectInteractor;
    private ArrayList<Project> projects;

    public MainPresenterImpl(MainView view) {
        this.view = view;
        this.context = (Activity) view;
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
            view.supportInvalidateOptionsMenu();
            view.addProjectListFragment(projects);
        } else {
            Log.w("MainPresenterImpl", "No projects were returned from the server");
        }
    }

    @Override
    public void onGetProjectsError(int errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            FrameLayout contentView = (FrameLayout) context.findViewById(R.id.content_frame);
            View empty = context.getLayoutInflater().inflate(R.layout.list_item_empty, null);
            TextView textView = (TextView) empty.findViewById(R.id.message);
            textView.setText(ErrorMessage.COULD_NOT_LOAD_PROJECTS);
            contentView.addView(empty);
            view.addEmptyMessageFragment();
        }
    }
}
