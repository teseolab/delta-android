package no.ntnu.mikaelr.delta.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.AddSuggestionPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.PostCommentPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.AddSuggestionPresenter;
import no.ntnu.mikaelr.delta.presenter.signature.PostCommentPresenter;

public class PostCommentActivity extends AppCompatActivity {

    private PostCommentPresenter presenter;

    private EditText comment;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        this.presenter = new PostCommentPresenterImpl(this);

        initializeView();
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Ny kommentar");
    }

    // INITIALIZATION --------------------------------------------------------------------------------------------------

    private void initializeView() {
        comment = (EditText) findViewById(R.id.comment);
        comment.requestFocus();

        // Makes the keyboard appear
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_suggestion, menu);
        return true;
    }

    // ACTIVITY METHODS ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_task_done) {
            String commentText = this.comment.getText().toString();
            presenter.onDoneClick(commentText);
        } else {
            finish();
        }

        // Makes the keyboard disappear
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);

        return true;
    }

}
