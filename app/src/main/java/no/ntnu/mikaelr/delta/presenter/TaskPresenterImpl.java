package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.TaskResponse;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.presenter.signature.TaskPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.signature.TaskView;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class TaskPresenterImpl implements TaskPresenter, ProjectInteractorImpl.OnPostProjectResponseListener {

    private TaskView view;
    private AppCompatActivity context;

    private Task task;
    private int taskIndex;
    private int projectId;

    private SparseArray<String> scaleValues = new SparseArray<String>();

    ProjectInteractor projectInteractor;

    private final int PROJECT_RESPONSE_DIALOG = 0;

    public TaskPresenterImpl(TaskView view) {
        this.view = view;
        this.context = (AppCompatActivity) view;
        this.task = getTaskFromIntent();
        this.taskIndex = context.getIntent().getIntExtra("taskIndex", -1);
        this.projectId = context.getIntent().getIntExtra("projectId", -1);
        this.projectInteractor = new ProjectInteractorImpl();
        initializeScaleValues();
    }

    private void initializeScaleValues() {
        scaleValues.put(0, "Helt uenig");
        scaleValues.put(1, "Uenig");
        scaleValues.put(2, "Vet ikke");
        scaleValues.put(3, "Enig");
        scaleValues.put(4, "Helt enig");
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private Task getTaskFromIntent() {
        Intent intent = ((Activity) view).getIntent();
        return (Task) intent.getSerializableExtra("task");
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public void onTaskDoneClick() {
        postResponse(); // TODO: Validate input
    }

    private void postResponse() {
        switch (task.getTaskType()) {
            case TEXT_TASK:
                projectInteractor.postResponse(view.getTextTaskResponse(), this);
                break;
            case SCALE_TASK:
                projectInteractor.postResponse(view.getScaleTaskResponses(), this);
                break;
            case ALTERNATIVE_TASK:
                projectInteractor.postResponse(view.getAlternativeTaskResponses(), this);
                break;
            case ALTERNATIVE_TASK_MULTI:
                projectInteractor.postResponse(view.getAlternativeMultiTaskResponses(), this);
            case FIRST_TASK:
                goBackToMissionView();
                break;
        }
    }

//    private TaskResponse getTextTaskResponse() {
//        ArrayList<String> responses = new ArrayList<String>();
//        String response = view.getTextTaskResponse();
//        responses.add(response);
//        return createProjectResponse(responses);
//    }

//    private TaskResponse getScaleTaskResponses() {
//        ArrayList<String> response = view.getScaleTaskResponses();
//        return createProjectResponse(response);
//    }

//    private TaskResponse getAlternativeResponses() {
//        List<String> responses = view.getAlternativeTaskResponses();
//        ArrayList<String> responses = new ArrayList<String>();
//        responses.add(response);
//        return createProjectResponse(responses);
//    }

//    private TaskResponse createProjectResponse(ArrayList<String> response) {
//        TaskResponse taskResponse = new TaskResponse();
//        taskResponse.setResponse(response);
//        taskResponse.setProjectId(projectId);
//        taskResponse.setTaskId(task.getId());
//        return taskResponse;
//    }

    @Override
    public void onTaskCancelClick() {
        Intent intent = new Intent();
        intent.putExtra("action", Constants.TASK_CANCELLED);
        intent.putExtra("taskId", task.getId());
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    @Override
    public int getTaskIndex() {
        return taskIndex;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public String getScaleValue(int progress) {
        return scaleValues.get(progress);
    }

    @Override
    public String getTaskImageUri() {
        return task.getImageUri();
    }

    // OnPostProjectResponseListener -----------------------------------------------------------------------------------

    @Override
    public void onPostProjectResponseSuccess() {
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
        view.showSpinner(false);
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.showMessage(ErrorMessage.COULD_NOT_POST_RESPONSE, Toast.LENGTH_LONG);
        }
    }
}
