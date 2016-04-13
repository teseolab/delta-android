package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.mikaelr.delta.listener.TaskDismissedListener;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.view.TaskView;

public class TaskPresenterImpl implements TaskPresenter {

    private TaskView view;
    private Activity context;

    private Task task;

    public TaskPresenterImpl(TaskView view) {
        this.view = view;
        this.context = (Activity) view;
        this.task = getTaskFromIntent();
    }

    // Private methods -------------------------------------------------------------------------------------------------

    private Task getTaskFromIntent() {
        Intent intent = ((Activity) view).getIntent();
        return (Task) intent.getSerializableExtra("task");
    }

    // Interface methods -----------------------------------------------------------------------------------------------

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void onTaskDoneClick() {

        // TODO: Validate input, post response, notify MissionView

        Intent intent = new Intent();
        intent.putExtra("action", Constants.TASK_FINISHED);
        intent.putExtra("taskId", task.getId());
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    @Override
    public void onTaskCancelClick() {
        Intent intent = new Intent();
        intent.putExtra("action", Constants.TASK_CANCELLED);
        intent.putExtra("taskId", task.getId());
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }
}
