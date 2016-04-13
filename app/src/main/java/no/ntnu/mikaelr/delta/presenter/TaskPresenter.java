package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.model.Task;

public interface TaskPresenter {

    Task getTask();

    void onTaskDoneClick();
    void onTaskCancelClick();

}
