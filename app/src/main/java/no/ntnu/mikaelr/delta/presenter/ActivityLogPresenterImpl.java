package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.LogRecord;
import no.ntnu.mikaelr.delta.presenter.signature.ActivityLogPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.signature.ActivityLogView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ActivityLogPresenterImpl implements ActivityLogPresenter, ProjectInteractorImpl.OnGetLogRecordsListener {

    private ActivityLogView view;
    private Activity context;
    private ProjectInteractor interactor;

    private List<LogRecord> logRecords;

    public ActivityLogPresenterImpl(ActivityLogView view) {
        this.view = view;
        this.context = ((Fragment) view).getActivity();
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
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.setEmptyListMessage(ErrorMessage.COULD_NOT_LOAD_ACTIVITY_LOG);
        }
    }
}
