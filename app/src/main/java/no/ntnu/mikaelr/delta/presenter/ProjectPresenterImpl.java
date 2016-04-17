package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import no.ntnu.mikaelr.delta.R;
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

    ProjectInteractor projectInteractor;

    Boolean missionIsCompleted;

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
        project.setLatitude(intent.getFloatExtra("latitude", -1));
        project.setLongitude(intent.getFloatExtra("longitude", -1));
        return project;
    }

    private void goToMission() {
        if (missionIsCompleted != null) {
            if (missionIsCompleted) {
                String title = "Heisann!";
                String message = "Du har allerede fullført dette oppdraget. Hva med å heller poste et forslag?";
                SimpleDialog.createAndShow(context.getSupportFragmentManager(), title, message);
            } else {
                Intent intent = new Intent(context, MissionActivity.class);
                intent.putExtra("projectId", project.getId());
                intent.putExtra("projectName", project.getName());
                intent.putExtra("latitude", project.getLatitude());
                intent.putExtra("longitude", project.getLongitude());
                context.startActivity(intent);
            }
        }
    }

    private void goToAddSuggestion() {
        Intent intent = new Intent(context, AddSuggestionActivity.class);
        intent.putExtra("projectId", project.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    private void goToSuggestionList() {
        Intent intent = new Intent(context, SuggestionListActivity.class);
        intent.putExtra("projectId", project.getId());
        context.startActivity(intent);
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setMissionCompletionStatus() {
        SharedPreferences preferences = context.getSharedPreferences("no.ntnu.mikaelr.delta", Context.MODE_PRIVATE);
        String missionCompletedPreference = preferences.getString("PROJECT_" + project.getId() + "_MISSION_COMPLETED", Constants.NA);

        if (missionCompletedPreference.equals(Constants.YES)) {
            missionIsCompleted = true;
        } else if (missionCompletedPreference.equals(Constants.NO)) {
            missionIsCompleted = false;
        } else if (missionCompletedPreference.equals(Constants.NA)) {
            projectInteractor.getMissionForProjectIsCompletedByUser(project.getId(), 1, this); //TODO: User id
        }
    }

    @Override
    public void onButtonClick(View view) {

        switch (view.getId()) {

            case R.id.start_mission_button:
                goToMission();
                break;
            case R.id.post_suggestion_button:
                goToAddSuggestion();
                break;
            case R.id.browse_suggestions_button:
                goToSuggestionList();
                break;
        }
    }

    @Override
    public void onGetMissionForProjectIsCompletedByUserSuccess(Boolean missionIsCompleted) {
        this.missionIsCompleted = missionIsCompleted;
        String yesOrNo = missionIsCompleted ? Constants.YES : Constants.NO;
        SharedPrefsUtil.saveMissionCompletionStatus(context, project.getId(), yesOrNo);
    }

    @Override
    public void onGetMissionForProjectIsCompletedByUserError(Integer errorCode) {
        SharedPrefsUtil.saveMissionCompletionStatus(context, project.getId(), Constants.NO);
        String title = "Ops!";
        String message = (errorCode == StatusCode.NETWORK_UNREACHABLE ? "Kunne ikke sende responsen, siden du mangler tilkobling til Internett." : "Det har skjedd en merkelig feil.");
        SimpleDialog.createAndShow(context.getSupportFragmentManager(), title, message);
    }
}
