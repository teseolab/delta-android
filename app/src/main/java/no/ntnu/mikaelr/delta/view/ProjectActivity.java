package no.ntnu.mikaelr.delta.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.SimpleDialog;
import no.ntnu.mikaelr.delta.presenter.signature.ProjectPresenter;
import no.ntnu.mikaelr.delta.presenter.ProjectPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.ProjectView;

public class ProjectActivity extends AppCompatActivity implements ProjectView, View.OnClickListener {

    private ProjectPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        this.presenter = new ProjectPresenterImpl(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_arrow_back_white_24dp, presenter.getProject().getName());
        initializeView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setMissionCompletionStatus();
    }

    // Initialization methods ------------------------------------------------------------------------------------------

    private void initializeView() {
        ImageView image = (ImageView) findViewById(R.id.project_image);
        TextView description = (TextView) findViewById(R.id.project_description);
        TextView startMissionButton = (TextView) findViewById(R.id.start_mission_button);
        if (!presenter.getProject().isMissionEnabled()) {
            startMissionButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.explore_gray, 0, 0);
        }
        TextView postSuggestionButton = (TextView) findViewById(R.id.post_suggestion_button);
        TextView browseSuggestionsButton = (TextView) findViewById(R.id.browse_suggestions_button);

        startMissionButton.setOnClickListener(this);
        postSuggestionButton.setOnClickListener(this);
        browseSuggestionsButton.setOnClickListener(this);

        String imageUri = presenter.getProject().getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        }
        description.setText(presenter.getProject().getDescription());
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public void showDialog(String title, String message) {
        SimpleDialog dialog = SimpleDialog.newInstance(title, message);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(dialog, null);
        transaction.commitAllowingStateLoss();
    }

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            presenter.onActivityResult(requestCode);
        }
    }


    // Listeners -------------------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_mission_button:
                presenter.goToMission();
                break;
            case R.id.post_suggestion_button:
                presenter.goToAddSuggestion();
                break;
            case R.id.browse_suggestions_button:
                presenter.goToSuggestionList();
                break;
        }
    }
}
