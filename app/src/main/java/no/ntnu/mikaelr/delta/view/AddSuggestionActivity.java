package no.ntnu.mikaelr.delta.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.AddImageDialog;
import no.ntnu.mikaelr.delta.fragment.ImageViewerDialog;
import no.ntnu.mikaelr.delta.presenter.AddSuggestionPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.AddSuggestionPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.view.signature.AddSuggestionView;

public class AddSuggestionActivity extends AppCompatActivity implements AddSuggestionView, View.OnClickListener, AddImageDialog.AddImageDialogListener {

    private AddSuggestionPresenter presenter;

    private EditText title;
    private EditText details;
    private ImageView image;
    private AppCompatButton addImageButton;

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
        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(this);
        addImageButton = (AppCompatButton) findViewById(R.id.add_image_button);
        ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
        addImageButton.setSupportBackgroundTintList(csl);
        addImageButton.setOnClickListener(this);

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

    // INTENT RESULT ---------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_image_button) {
            // Makes the keyboard disappear
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
            AddImageDialog.newInstance().show(getSupportFragmentManager(), "tag");
        }
    }

    @Override
    public void onTakePhotoClicked(Dialog dialog) {
        dialog.dismiss();
        presenter.openCamera(Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    public void onSelectPhotoClicked(Dialog dialog) {
        dialog.dismiss();
        presenter.openGallery(Constants.CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public String getSuggestionTitle() {
        return title.getText().toString();
    }

    @Override
    public String getSuggestionDetails() {
        return details.getText().toString();
    }

    @Override
    public void setSuggestionImage(Bitmap bitmap) {
        image.setVisibility(View.VISIBLE);
        image.setImageBitmap(bitmap);
    }

    @Override
    public void setButtonText(String text) {
        addImageButton.setText(text);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
