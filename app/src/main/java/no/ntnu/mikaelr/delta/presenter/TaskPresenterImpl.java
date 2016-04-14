package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.ProjectResponse;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.view.TaskView;
import no.ntnu.mikaelr.delta.view.ToolbarUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TaskPresenterImpl implements TaskPresenter, SeekBar.OnSeekBarChangeListener, ProjectInteractorImpl.OnPostProjectResponseListener {

    private TaskView view;
    private AppCompatActivity context;

    private Task task;
    private int taskIndex;
    private int projectId;

    private ArrayList<View> scaleTaskViews = new ArrayList<View>();
    private SparseArray<String> scaleValues = new SparseArray<String>();

    ProjectInteractor projectInteractor;

    public TaskPresenterImpl(TaskView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.task = getTaskFromIntent();
        this.taskIndex = context.getIntent().getIntExtra("taskIndex", -1);
        this.projectId = context.getIntent().getIntExtra("projectId", -1);
        this.projectInteractor = new ProjectInteractorImpl();
        initializeView();
    }

    private void initializeScaleValues() {
        scaleValues.put(0, "Helt uenig");
        scaleValues.put(1, "Uenig");
        scaleValues.put(2, "Vet ikke");
        scaleValues.put(3, "Enig");
        scaleValues.put(4, "Helt enig");
    }

    private void initializeView() {

        switch (task.getTaskType()) {

            case FIRST_TASK:
                initializeFirstTaskView();
                break;
            case TEXT_TASK:
                initializeTextTaskView();
                break;
            case SCALE_TASK:
                initializeScaleValues();
                initializeScaleTaskView();
                break;
        }
    }

    private void initializeScaleTaskView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_task_scale, null);
        context.setContentView(view);

        ToolbarUtil.initializeToolbar(context, R.drawable.ic_close_white_24dp, "Oppgave " + taskIndex);

        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout_task_scale);

        for (String taskDescription : task.getDescriptions()) {

            View scaleTaskView = inflater.inflate(R.layout.task_scale, null);
            TextView scaleTaskTextView = (TextView) scaleTaskView.findViewById(R.id.task_scale_text);
            scaleTaskTextView.setText(taskDescription);

            parentLayout.addView(scaleTaskView);
            scaleTaskViews.add(scaleTaskView);

            SeekBar seekBar = (SeekBar) scaleTaskView.findViewById(R.id.seekbar);
            seekBar.setOnSeekBarChangeListener(this);

            TextView textView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            textView.setText(scaleValues.get(seekBar.getProgress()));

        }
    }

    private void initializeTextTaskView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_task_text, null);
        TextView textTaskTextView = (TextView) view.findViewById(R.id.text_task_text);
        textTaskTextView.setText(task.getDescriptions().get(0));
        context.setContentView(view);

        ToolbarUtil.initializeToolbar(context, R.drawable.ic_close_white_24dp, "Oppgave " + taskIndex);
    }

    private void initializeFirstTaskView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_task_first, null);
        TextView firstTaskTextView = (TextView) view.findViewById(R.id.first_task_text);
        firstTaskTextView.setText(task.getDescriptions().get(0));
        context.setContentView(view);

        ToolbarUtil.initializeToolbar(context, R.drawable.ic_close_white_24dp, "Klar, ferdig, gå!");
    }

    private void setSeekBarText(SeekBar seekBar, int progress) {
        View parent = (View) seekBar.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.seekbar_text);
        textView.setText(scaleValues.get(progress));
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private Task getTaskFromIntent() {
        Intent intent = ((Activity) view).getIntent();
        return (Task) intent.getSerializableExtra("task");
    }

    // Seek bar listener -----------------------------------------------------------------------------------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setSeekBarText(seekBar, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void onTaskDoneClick() {

        // TODO: Validate input, post response, notify MissionView

        postResponse();


    }

    private void postResponse() {
        switch (task.getTaskType()) {
            case TEXT_TASK:
                projectInteractor.postResponse(getTextTaskResponse(), this);
                break;
            case SCALE_TASK:
                projectInteractor.postResponse(getScaleTaskResponses(), this);
                break;
            case FIRST_TASK:
                goBackToMissionView();
                break;
        }
    }

    private ProjectResponse getTextTaskResponse() {
        ArrayList<String> response = new ArrayList<String>();
        EditText textTaskEditText = (EditText) context.findViewById(R.id.text_task_response);
        response.add(textTaskEditText.getText().toString());
        return createProjectResponse(response);
    }

    private ProjectResponse getScaleTaskResponses() {
        ArrayList<String> response = new ArrayList<String>();
        for (View scaleTaskView : scaleTaskViews) {
            TextView seekBarTextView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            response.add(seekBarTextView.getText().toString());
        }
        return createProjectResponse(response);
    }

    private ProjectResponse createProjectResponse(ArrayList<String> response) {
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setResponse(response);
        projectResponse.setUserId(1); //TODO: Implement
        projectResponse.setProjectId(projectId);
        projectResponse.setTaskId(task.getId());
        return projectResponse;
    }

    @Override
    public void onTaskCancelClick() {
        Intent intent = new Intent();
        intent.putExtra("action", Constants.TASK_CANCELLED);
        intent.putExtra("taskId", task.getId());
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    // OnPostProjectResponseListener -----------------------------------------------------------------------------------

    @Override
    public void onPostProjectResponseSuccess(JSONObject jsonObject) {

        goBackToMissionView();

    }

    private void goBackToMissionView() {
        Intent intent = new Intent();
        intent.putExtra("action", Constants.TASK_FINISHED);
        intent.putExtra("taskId", task.getId());
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    @Override
    public void onPostProjectResponseError(Integer errorCode) {

    }
}
