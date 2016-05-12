package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.presenter.signature.PostCommentPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.PostCommentActivity;
import no.ntnu.mikaelr.delta.view.signature.PostCommentView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class PostCommentPresenterImpl implements PostCommentPresenter, ProjectInteractorImpl.OnPostCommentListener {

    private PostCommentView view;
    private Activity context;
    private ProjectInteractor interactor;

    private int suggestionId;

    public PostCommentPresenterImpl(PostCommentView view) {
        this.view = view;
        this.context = (Activity) view;
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
        ArrayList<Comment> comments = JsonFormatter.formatComments(jsonArray);
        Intent intent = new Intent();
        intent.putExtra("comments", comments);
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
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
}
