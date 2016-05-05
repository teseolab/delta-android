package no.ntnu.mikaelr.delta.presenter.signature;

import android.view.View;
import no.ntnu.mikaelr.delta.model.Project;

public interface ProjectPresenter {

    Project getProject();

    void goToMission();
    void goToAddSuggestion();
    void goToSuggestionList();

    void setMissionCompletionStatus();

    void onActivityResult(int requestCode);
}
