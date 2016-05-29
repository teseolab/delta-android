package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.TaskResponse;

import java.util.List;

public interface TaskView {
    void showSpinner(boolean showSpinner);
    List<TaskResponse> getTextTaskResponse();
    List<TaskResponse> getScaleTaskResponses();
    List<TaskResponse> getAlternativeTaskResponses();

    void showMessage(String message, int length);
}
