package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.presenter.signature.PostCommentPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.signature.PostCommentView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class PostCommentPresenterImpl implements PostCommentPresenter, ProjectInteractorImpl.OnPostCommentListener, ProjectInteractorImpl.OnGetCommentAchievementListener {

    private PostCommentView view;
    private AppCompatActivity context;
    private ProjectInteractor interactor;

    private int suggestionId;
    private ArrayList<Comment> comments;

    public PostCommentPresenterImpl(PostCommentView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.interactor = new ProjectInteractorImpl();
        this.suggestionId = context.getIntent().getIntExtra("suggestionId", -1);
    }

    // INTERFACE METHODS

    @Override
    public void onDoneClick(String comment) {
        if (suggestionId != -1) {
            if (!comment.equals("")) {
                view.showSpinner(true);
                interactor.postComment(comment, suggestionId, this);
            } else {
                view.showMessage(ErrorMessage.COMMENT_CANNOT_BE_EMPTY, Toast.LENGTH_LONG);
            }
        }
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onPostCommentSuccess(JSONArray jsonArray) {
        comments = JsonFormatter.formatComments(jsonArray);
        interactor.getCommentAchievement(this);
        Log.i("PostCommentPresenter", "Successfully posted comment");
    }

    @Override
    public void onPostCommentError(int errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.showSpinner(false);
            view.showMessage(ErrorMessage.COULD_NOT_POST_COMMENT, Toast.LENGTH_LONG);
            Log.w("PostCommentPresenter", ErrorMessage.COULD_NOT_POST_COMMENT + ". Error " + errorCode);
        }
    }

    @Override
    public void onGetCommentAchievementSuccess(String result) {
        Intent intent = new Intent();
        if (result != null) {
            Achievement achievement = JsonFormatter.formatAchievement(result);
            intent.putExtra("achievement", achievement);
        }
        intent.putExtra("comments", comments);
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    @Override
    public void onGetCommentAchievementError(int errorCode) {
        Intent intent = new Intent();
        intent.putExtra("comments", comments);
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
        Log.w("PostCommentPresenter", "Could not get comment count" + ". Error " + errorCode);
    }



}
