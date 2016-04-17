package no.ntnu.mikaelr.delta.presenter.signature;

import android.view.View;
import no.ntnu.mikaelr.delta.model.Project;

public interface ProjectPresenter {

    void onButtonClick(View view);

    Project getProject();

    void setMissionCompletionStatus();
}
