package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.ImageViewerDialog;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.TaskPresenter;
import no.ntnu.mikaelr.delta.presenter.TaskPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.TaskView;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements TaskView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private TaskPresenter presenter;

    private ArrayList<View> scaleTaskViews = new ArrayList<View>();

    private boolean responseIsBeingPosted = false;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = new TaskPresenterImpl(this);
        initializeView();
    }

    // OPTIONS MENU ----------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem doneButton = menu.findItem(R.id.action_task_done);
        ToolbarUtil.showSpinner(doneButton, responseIsBeingPosted);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_task_done) {
            showSpinner(true);
            presenter.onTaskDoneClick();
        } else {
            presenter.onTaskCancelClick();
        }
        return true;
    }

    // VIEW INITIALIZATION ---------------------------------------------------------------------------------------------

    private void initializeView() {

        int taskIndex = presenter.getTaskIndex();
        Task task = presenter.getTask();

        switch (task.getTaskType()) {

            case FIRST_TASK:
                initializeFirstTaskView(task);
                break;
            case TEXT_TASK:
                initializeTextTaskView(taskIndex, task);
                break;
            case SCALE_TASK:
                initializeScaleTaskView(taskIndex, task);
                break;
        }
    }

    private void initializeFirstTaskView(Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_first, null);

        TextView firstTaskTextView = (TextView) taskView.findViewById(R.id.first_task_text);
        firstTaskTextView.setText(task.getDescriptions().get(0));

        setContentView(taskView);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Klar, ferdig, gå!");
    }

    private void initializeScaleTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_scale, null);

        LinearLayout parentLayout = (LinearLayout) taskView.findViewById(R.id.parent_layout_task_scale);
        View imageWrapper = parentLayout.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) parentLayout.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        for (String taskDescription : task.getDescriptions()) {

            View scaleTaskView = inflater.inflate(R.layout.task_scale_item, null);
            TextView scaleTaskTextView = (TextView) scaleTaskView.findViewById(R.id.task_scale_text);
            scaleTaskTextView.setText(taskDescription);

            parentLayout.addView(scaleTaskView);
            scaleTaskViews.add(scaleTaskView);

            SeekBar seekBar = (SeekBar) scaleTaskView.findViewById(R.id.seekbar);
            seekBar.setOnSeekBarChangeListener(this);

            TextView textView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            String scaleValue = presenter.getScaleValue(seekBar.getProgress());
            textView.setText(scaleValue);
        }

        setContentView(taskView);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskIndex);
    }

    private void initializeTextTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_text, null);

        View imageWrapper = taskView.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) taskView.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        TextView textTaskTextView = (TextView) taskView.findViewById(R.id.text_task_text);
        textTaskTextView.setText(task.getDescriptions().get(0));

        setContentView(taskView);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskIndex);
    }

    // VIEW INTERFACE --------------------------------------------------------------------------------------------------

    @Override
    public void showSpinner(boolean showSpinner) {
        responseIsBeingPosted = showSpinner;
        supportInvalidateOptionsMenu();
    }

    @Override
    public String getTextTaskResponse() {
        EditText editText = (EditText) findViewById(R.id.text_task_response);
        return editText.getText().toString();
    }

    @Override
    public ArrayList<String> getScaleTaskResponse() {
        ArrayList<String> response = new ArrayList<String>();
        for (View scaleTaskView : scaleTaskViews) {
            TextView seekBarTextView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            response.add(seekBarTextView.getText().toString());
        }
        return response;
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    // SEEK BAR LISTENER -----------------------------------------------------------------------------------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        View parent = (View) seekBar.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.seekbar_text);
        textView.setText(presenter.getScaleValue(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    // ON CLICK LISTENER -----------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image) {
            ImageViewerDialog.newInstance(presenter.getTaskImageUri()).show(getSupportFragmentManager(), null);
        }
    }
}
