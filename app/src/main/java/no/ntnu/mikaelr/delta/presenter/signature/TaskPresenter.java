package no.ntnu.mikaelr.delta.presenter.signature;

import no.ntnu.mikaelr.delta.model.Task;

public interface TaskPresenter {

    void onTaskDoneClick();
    void onTaskCancelClick();

    int getTaskIndex();
    Task getTask();
    String getTaskImageUri();
    String getScaleValue(int progress);

}
