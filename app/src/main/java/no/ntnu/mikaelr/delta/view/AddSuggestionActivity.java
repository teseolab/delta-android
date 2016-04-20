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
import no.ntnu.mikaelr.delta.presenter.signature.AddSuggestionPresenter;

public class AddSuggestionActivity extends AppCompatActivity {

    private AddSuggestionPresenter presenter;

    private EditText title;
    private EditText details;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suggestion);
        this.presenter = new AddSuggestionPresenterImpl(this);

        initializeView();
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Nytt forslag");
    }

    // INITIALIZATION --------------------------------------------------------------------------------------------------

    private void initializeView() {
        title = (EditText) findViewById(R.id.title);
        title.requestFocus();
        details = (EditText) findViewById(R.id.details);
        AppCompatButton v = (AppCompatButton) findViewById(R.id.add_image_button);
        ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
        v.setSupportBackgroundTintList(csl);

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
            String title = this.title.getText().toString();
            String details = this.details.getText().toString();
            presenter.onDoneClick(title, details);
        } else {
            finish();
        }

        // Makes the keyboard disappear
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);

        return true;
    }

}
