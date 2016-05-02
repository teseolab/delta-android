package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.LogRecord;
import no.ntnu.mikaelr.delta.presenter.signature.ActivityLogPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.view.signature.ActivityLogView;
import org.json.JSONArray;

import java.util.List;

public class ActivityLogPresenterImpl implements ActivityLogPresenter, ProjectInteractorImpl.OnGetLogRecordsListener {

    private ActivityLogView view;
    private ProjectInteractor interactor;

    private List<LogRecord> logRecords;

    public ActivityLogPresenterImpl(ActivityLogView view) {
        this.view = view;
        this.interactor = new ProjectInteractorImpl();
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void loadLogRecords() {
        interactor.getLogRecords(this);
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onGetLogRecordsSuccess(JSONArray jsonArray) {
        logRecords = LogRecord.fromJsonArray(jsonArray);
        view.updateView(logRecords);
        if (logRecords.size() == 0) {
            view.setEmptyListMessage(ErrorMessage.NO_ACTIVITIES_IN_LOG);
        }
    }

    @Override
    public void onGetLogRecordsError(int errorCode) {
        view.setEmptyListMessage(ErrorMessage.COULD_NOT_LOAD_ACTIVITY_LOG);
    }
}
