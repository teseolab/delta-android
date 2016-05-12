package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.TopListPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.signature.TopListView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.util.List;

public class TopListPresenterImpl implements TopListPresenter, ProjectInteractorImpl.OnFinishedLoadingTopList {

    private TopListView view;
    private Activity context;
    private List<HighscoreUser> topList;
    private ProjectInteractor projectInteractor;

    public TopListPresenterImpl(TopListView view) {
        this.view = view;
        this.context = ((Fragment) view).getActivity();
        this.projectInteractor = new ProjectInteractorImpl();
    }


    // PRESENTER INTERFACE ---------------------------------------------------------------------------------------------

    @Override
    public void loadTopList() {
        projectInteractor.getTopList(this);
    }


    // ASYNC TASK LISTENERS --------------------------------------------------------------------------------------------


    @Override
    public void onLoadTopListSuccess(JSONArray jsonArray) {
        topList = HighscoreUser.fromJsonArray(jsonArray);
        view.updateList(topList);
    }

    @Override
    public void onLoadTopListError(int errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context);
        } else {
            view.setEmptyListMessage(ErrorMessage.COULD_NOT_LOAD_TOP_LIST);
        }
    }

}
