package no.ntnu.mikaelr.delta.listener;

public interface TaskDismissedListener {

    void onTaskFinished(int taskId);
    void onTaskCancelled(int taskId);

}
