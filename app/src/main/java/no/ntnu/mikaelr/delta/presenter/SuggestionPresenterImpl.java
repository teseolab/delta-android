package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.signature.SuggestionView;
import org.json.JSONArray;

import java.util.List;

public class SuggestionPresenterImpl implements SuggestionPresenter, ProjectInteractorImpl.OnFinishedLoadingCommentsListener, ProjectInteractorImpl.OnPostAgreementListener, ProjectInteractorImpl.OnPostDisagreementListener {

    private SuggestionView view;
    private Activity context;
    private ProjectInteractor projectInteractor;

    private Suggestion suggestion;

    public SuggestionPresenterImpl(SuggestionView view) {
        this.view = view;
        this.context = (Activity) view;
        this.projectInteractor = new ProjectInteractorImpl();
        this.suggestion = getSuggestionFromIntent();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private Suggestion getSuggestionFromIntent() {
        Intent intent = context.getIntent();
        return (Suggestion) intent.getSerializableExtra("suggestion");
    }

    // INTERFACE -------------------------------------------------------------------------------------------------------

    @Override
    public void loadComments(int suggestionId) {
        projectInteractor.getComments(suggestion.getId(), this);
    }

    @Override
    public Suggestion getSuggestion() {
        return suggestion;
    }

    @Override
    public boolean userAgrees() {
        return suggestion.getAgrees().equals(Constants.YES);
    }

    @Override
    public boolean userDisagrees() {
        return suggestion.getAgrees().equals(Constants.NO);
    }

    @Override
    public void postAgreement() {
        projectInteractor.postAgreement(suggestion.getId(), this);
    }

    @Override
    public void postDisagreement() {
        projectInteractor.postDisagreement(suggestion.getId(), this);
    }

    @Override
    public void onFinished() {
        Intent intent = new Intent();
        intent.putExtra("suggestion", suggestion);
        intent.putExtra("suggestionIndex", context.getIntent().getIntExtra("suggestionIndex", -1));
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    // ASYNC TASK LISTENERS --------------------------------------------------------------------------------------------

    @Override
    public void onFinishedLoadingCommentsSuccess(JSONArray jsonArray) {
        List<Comment> comments = JsonFormatter.formatComments(jsonArray);
        view.updateComments(comments);
        if (comments.size() == 0) {
            view.setEmptyListMessage("Det er ingen kommentarer enda. Bli den første til å skrive noe!");
        }
    }

    @Override
    public void onFinishedLoadingCommentsError(Integer errorCode) {
        view.setEmptyListMessage("Kunne ikke laste inn kommentarer :(");
    }

    @Override
    public void onPostAgreementSuccess(int agreements, int disagreements) {
        suggestion.setAgrees(Constants.YES);
        suggestion.setAgreements(agreements);
        suggestion.setDisagreements(disagreements);
        view.updateAgreements();
        view.setSelectedButtonThumbsUp();
        view.enableAgreementButtonsClickListener(true);
    }

    @Override
    public void onPostDisagreementSuccess(int agreements, int disagreements) {
        suggestion.setAgrees(Constants.NO);
        suggestion.setAgreements(agreements);
        suggestion.setDisagreements(disagreements);
        view.updateAgreements();
        view.setSelectedButtonThumbsDown();
        view.enableAgreementButtonsClickListener(true);
    }

    @Override
    public void onPostAgreementError(Integer errorCode) {
        System.out.println("POST AGREEMENT ERROR");
    }

    @Override
    public void onPostDisagreementError(Integer errorCode) {
        System.out.println("POST DISAGREEMENT ERROR");
    }
}
