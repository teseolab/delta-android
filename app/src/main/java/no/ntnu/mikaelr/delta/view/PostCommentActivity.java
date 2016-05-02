package no.ntnu.mikaelr.delta.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.PostCommentPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.PostCommentPresenter;
import no.ntnu.mikaelr.delta.view.signature.PostCommentView;

public class PostCommentActivity extends AppCompatActivity implements PostCommentView {

    private PostCommentPresenter presenter;
    private EditText comment;
    private boolean commentIsBeingPosted = false;

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem doneButton = menu.findItem(R.id.action_task_done);
        ToolbarUtil.showSpinner(doneButton, commentIsBeingPosted);
        return true;
    }

    // ACTIVITY METHODS ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_task_done) {
            showSpinner(true);
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

    @Override
    public void showSpinner(boolean showSpinner) {
        commentIsBeingPosted = showSpinner;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

}
