package no.ntnu.mikaelr.delta.presenter;

import android.content.Intent;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.AddSuggestionPresenter;
import no.ntnu.mikaelr.delta.view.AddSuggestionActivity;
import no.ntnu.mikaelr.delta.view.SuggestionListActivity;
import org.json.JSONObject;

import java.util.Calendar;

public class AddSuggestionPresenterImpl implements AddSuggestionPresenter, ProjectInteractorImpl.OnPostSuggestionListener {

    private AddSuggestionActivity view;
    private ProjectInteractor interactor;
    private int projectId;

    public AddSuggestionPresenterImpl(AddSuggestionActivity view) {
        this.view = view;
        this.interactor = new ProjectInteractorImpl();
        this.projectId = view.getIntent().getIntExtra("projectId", -1);
    }

    // INTERFACE METHODS

    @Override
    public void onDoneClick(String title, String details) {
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(title);
        suggestion.setDetails(details);
        suggestion.setDate(Calendar.getInstance().getTime());
        suggestion.setImageUri("no image"); // TODO: Image
        suggestion.setProjectId(projectId);
        interactor.postSuggestion(suggestion, this);
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onPostSuggestionSuccess(JSONObject jsonSuggestion) {
        if (projectId != -1) {
            Intent intent = new Intent(view, SuggestionListActivity.class);
            intent.putExtra("projectId", projectId);
            view.startActivity(intent);
        }
    }

    @Override
    public void onPostSuggestionError(int errorCode) {
        System.out.println(errorCode);
    }
}
