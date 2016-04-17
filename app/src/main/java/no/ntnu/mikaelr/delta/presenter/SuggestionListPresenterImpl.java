package no.ntnu.mikaelr.delta.presenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionListPresenter;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.SuggestionActivity;
import no.ntnu.mikaelr.delta.view.signature.SuggestionListView;
import org.json.JSONArray;

import java.util.List;

public class SuggestionListPresenterImpl implements SuggestionListPresenter, ProjectInteractorImpl.OnFinishedLoadingSuggestionsListener {

    private SuggestionListView view;
    private AppCompatActivity context;

    private int projectId;
    private List<Suggestion> suggestions;

    private ProjectInteractor projectInteractor;

    private final int SUGGESTION_RESULT = 0;

    public SuggestionListPresenterImpl(SuggestionListView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.projectInteractor = new ProjectInteractorImpl();
        this.projectId = getProjectIdFromIntent();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private int getProjectIdFromIntent() {
        return context.getIntent().getIntExtra("projectId", -1);
    }

    // PRESENTER INTERFACE ---------------------------------------------------------------------------------------------

    @Override
    public void loadSuggestions() {
        projectInteractor.getSuggestions(projectId, this);
    }

    // INTENT ----------------------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(int position) {
        Suggestion clickedSuggestion = suggestions.get(position);
        Intent intent = new Intent(context, SuggestionActivity.class);
        intent.putExtra("suggestion", clickedSuggestion);
        intent.putExtra("suggestionIndex", position);
        context.startActivityForResult(intent, SUGGESTION_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == SUGGESTION_RESULT) {
            Suggestion updatedSuggestion = (Suggestion) data.getSerializableExtra("suggestion");
            int updatedSuggestionIndex = data.getIntExtra("suggestionIndex", -1);
            if (updatedSuggestionIndex >= 0) {
                suggestions.set(updatedSuggestionIndex, updatedSuggestion);
            }
        }
    }

    // ASYNC TASK LISTENERS --------------------------------------------------------------------------------------------

    @Override
    public void onFinishedLoadingSuggestionsSuccess(JSONArray jsonArray) {
        suggestions = JsonFormatter.formatSuggestions(jsonArray);
        view.updateList(suggestions);
    }

    @Override
    public void onFinishedLoadingSuggestionsError(Integer errorCode) {

    }
}
