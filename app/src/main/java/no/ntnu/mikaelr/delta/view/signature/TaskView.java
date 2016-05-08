package no.ntnu.mikaelr.delta.view.signature;

import java.util.ArrayList;

public interface TaskView {
    void showSpinner(boolean showSpinner);
    String getTextTaskResponse();
    ArrayList<String> getScaleTaskResponse();
}
