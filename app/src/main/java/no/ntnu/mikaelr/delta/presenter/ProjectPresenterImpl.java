package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.view.*;

public class ProjectPresenterImpl implements ProjectPresenter {

    private ProjectView view;
    private Activity context;
    private Project project;

    public ProjectPresenterImpl(ProjectView view) {
        this.view = view;
        this.context = (Activity) view;
        this.project = getProjectFromIntent();
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
        Intent intent = new Intent(context, MissionActivity.class);
        intent.putExtra("projectId", project.getId());
        intent.putExtra("projectName", project.getName());
        intent.putExtra("latitude", project.getLatitude());
        intent.putExtra("longitude", project.getLongitude());
        context.startActivity(intent);
    }

    private void goToAddSuggestion() {
        Intent intent = new Intent(context, AddSuggestionActivity.class);
        context.startActivity(intent);
    }

    private void goToSuggestionList() {
        Intent intent = new Intent(context, SuggestionListActivity.class);
        context.startActivity(intent);
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public Project getProject() {
        return project;
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
}
