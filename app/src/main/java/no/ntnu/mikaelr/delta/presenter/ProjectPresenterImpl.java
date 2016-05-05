package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import no.ntnu.mikaelr.delta.fragment.SimpleDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.signature.ProjectPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.util.StatusCode;
import no.ntnu.mikaelr.delta.view.*;
import no.ntnu.mikaelr.delta.view.signature.ProjectView;

public class ProjectPresenterImpl implements ProjectPresenter, ProjectInteractorImpl.OnGetMissionForProjectIsCompletedByUser {

    private ProjectView view;
    private AppCompatActivity context;
    private Project project;

    private ProjectInteractor projectInteractor;

    private Boolean missionIsCompleted;

    private int MISSION_REQUEST = 0;

    public ProjectPresenterImpl(ProjectView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.project = getProjectFromIntent();
        this.projectInteractor = new ProjectInteractorImpl();
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private Project getProjectFromIntent() {
        Intent intent = ((Activity) view).getIntent();
        Project project = new Project();
        project.setId(intent.getIntExtra("id", -1));
        project.setName(intent.getStringExtra("name"));
        project.setDescription(intent.getStringExtra("description"));
        project.setImageUri(intent.getStringExtra("imageUri"));
        project.setLatitude(intent.getFloatExtra("latitude", -1));
        project.setLongitude(intent.getFloatExtra("longitude", -1));
        return project;
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setMissionCompletionStatus() {
        String username = SharedPrefsUtil.getInstance().getUsername();
        String missionCompletedPreference = SharedPrefsUtil.getInstance().getMissionCompletionStatus(project.getId(), username);

        if (missionCompletedPreference.equals(Constants.YES)) {
            missionIsCompleted = true;
        } else if (missionCompletedPreference.equals(Constants.NO)) {
            missionIsCompleted = false;
        } else if (missionCompletedPreference.equals(Constants.NA)) {
            projectInteractor.getMissionForProjectIsCompletedByUser(project.getId(), this);
        }
    }

    @Override
    public void goToMission() {
        if (missionIsCompleted != null) {
            if (missionIsCompleted) {
                String title = "Heisann!";
                String message = "Du har allerede fullført dette oppdraget. Hva med å heller poste et forslag?";
                SimpleDialog.newInstance(title, message).show(context.getSupportFragmentManager(), null);
            } else {
                Intent intent = new Intent(context, MissionActivity.class);
                intent.putExtra("projectId", project.getId());
                intent.putExtra("projectName", project.getName());
                intent.putExtra("latitude", project.getLatitude());
                intent.putExtra("longitude", project.getLongitude());
                context.startActivityForResult(intent, MISSION_REQUEST);
            }
        }
    }

    @Override
    public void goToAddSuggestion() {
        Intent intent = new Intent(context, AddSuggestionActivity.class);
        intent.putExtra("projectId", project.getId());
        context.startActivity(intent);
    }

    @Override
    public void goToSuggestionList() {
        Intent intent = new Intent(context, SuggestionListActivity.class);
        intent.putExtra("projectId", project.getId());
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode) {
        if (requestCode == MISSION_REQUEST) {
            view.showDialog("Gratulerer!", "Du har fullført dette oppdraget. Hva med å poste et forslag?");
        }
    }


    @Override
    public void onGetMissionForProjectIsCompletedByUserSuccess(Boolean missionIsCompleted) {
        this.missionIsCompleted = missionIsCompleted;
        String yesOrNo = missionIsCompleted ? Constants.YES : Constants.NO;
        String username = SharedPrefsUtil.getInstance().getUsername();
        SharedPrefsUtil.getInstance().setMissionCompletionStatus(project.getId(), username, yesOrNo);
    }

    @Override
    public void onGetMissionForProjectIsCompletedByUserError(Integer errorCode) {
        String username = SharedPrefsUtil.getInstance().getUsername();
        SharedPrefsUtil.getInstance().setMissionCompletionStatus(project.getId(), username, Constants.NO);
        String title = "Ops!";
        String message = (errorCode == StatusCode.NETWORK_UNREACHABLE ? "Kunne ikke sende responsen, siden du mangler tilkobling til Internett." : "Det har skjedd en merkelig feil.");
        SimpleDialog.createAndShow(context.getSupportFragmentManager(), title, message);
    }
}
