package no.ntnu.mikaelr.delta.presenter;

import android.support.v7.app.AppCompatActivity;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.SuggestionListView;
import org.json.JSONArray;

import java.util.List;

public class SuggestionListPresenterImpl implements SuggestionListPresenter, ProjectInteractorImpl.OnFinishedLoadingSuggestionsListener {

    private SuggestionListView view;
    private AppCompatActivity context;

    private int projectId;

    private ProjectInteractor projectInteractor;

    public SuggestionListPresenterImpl(SuggestionListView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.projectInteractor = new ProjectInteractorImpl();
        this.projectId = getProjectIdFromIntent();
    }

    private int getProjectIdFromIntent() {
        return context.getIntent().getIntExtra("projectId", -1);
    }

    @Override
    public void loadSuggestions() {
        projectInteractor.getSuggestions(projectId, this);
    }


    @Override
    public void onFinishedLoadingSuggestionsSuccess(JSONArray jsonArray) {
        List<Suggestion> suggestions = JsonFormatter.formatSuggestions(jsonArray);
    }

    @Override
    public void onFinishedLoadingSuggestionsError(Integer errorCode) {

    }
}
