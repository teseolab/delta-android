package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionPresenter;
import no.ntnu.mikaelr.delta.util.*;
import no.ntnu.mikaelr.delta.view.PostCommentActivity;
import no.ntnu.mikaelr.delta.view.signature.SuggestionView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class SuggestionPresenterImpl implements SuggestionPresenter, ProjectInteractorImpl.OnFinishedLoadingCommentsListener, ProjectInteractorImpl.OnPostAgreementListener, ProjectInteractorImpl.OnPostDisagreementListener {

    private SuggestionView view;
    private AppCompatActivity context;
    private ProjectInteractor projectInteractor;

    private Suggestion suggestion;

    private final int COMMENT_REQUEST = 1;

    public SuggestionPresenterImpl(SuggestionView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
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
    public void loadComments() {
        projectInteractor.getComments(suggestion.getId(), this);
    }

//    @Override
//    public void loadImage() {
//        projectInteractor.getImage(suggestion.getImageUri(), this);
//    }

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
    public boolean hasImage() {
        return !suggestion.getImageUri().equals("");
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

    @Override
    public void goToPostComment() {
        Intent intent = new Intent(context, PostCommentActivity.class);
        intent.putExtra("suggestionId", suggestion.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivityForResult(intent, COMMENT_REQUEST);
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == COMMENT_REQUEST) {
            Achievement achievement = (Achievement) data.getSerializableExtra("achievement");
            if (achievement != null) {
                FragmentManager fragmentManager = context.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(CustomDialog.newInstance(achievement.getName(), achievement.getDescription(), "OK", null,
                                BadgeIdConverter.getInstance().convertBadgeNameToResourceId(achievement.getBadgeName())),null)
                        .commitAllowingStateLoss();
            }
            @SuppressWarnings("unchecked")
            ArrayList<Comment> comments = (ArrayList<Comment>) data.getSerializableExtra("comments");
            view.updateComments(comments);
            view.hideEmptyListMessage();
        }
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
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.setEmptyListMessage(ErrorMessage.COULD_NOT_LOAD_COMMENTS);
        }
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
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.showMessage(ErrorMessage.COULD_NOT_POST_AGREEMENT, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPostDisagreementError(Integer errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.showMessage(ErrorMessage.COULD_NOT_POST_DISAGREEMENT, Toast.LENGTH_LONG);
        }
    }

}
