package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.presenter.signature.PostCommentPresenter;
import no.ntnu.mikaelr.delta.util.JsonFormatter;
import no.ntnu.mikaelr.delta.view.PostCommentActivity;
import org.json.JSONArray;

import java.util.ArrayList;

public class PostCommentPresenterImpl implements PostCommentPresenter, ProjectInteractorImpl.OnPostCommentListener {

    private PostCommentActivity view;
    private ProjectInteractor interactor;

    private int suggestionId;

    public PostCommentPresenterImpl(PostCommentActivity view) {
        this.view = view;
        this.interactor = new ProjectInteractorImpl();
        this.suggestionId = view.getIntent().getIntExtra("suggestionId", -1);
    }

    // INTERFACE METHODS

    @Override
    public void onDoneClick(String comment) {
        if (suggestionId != -1) {
            interactor.postComment(comment, suggestionId, this);
//            JSONObject jsonComment = new JSONObject();
//            try {
//                jsonComment.put("comment", comment);
//                interactor.postComment(jsonComment.toString(), suggestionId, this);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onPostCommentSuccess(JSONArray jsonArray) {
        ArrayList<Comment> comments = JsonFormatter.formatComments(jsonArray);
        Intent intent = new Intent();
        intent.putExtra("comments", comments);
        view.setResult(Activity.RESULT_OK, intent);
        view.finish();
    }

    @Override
    public void onPostCommentError(int errorCode) {

    }
}
