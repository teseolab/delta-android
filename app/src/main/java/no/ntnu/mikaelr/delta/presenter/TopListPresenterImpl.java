package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.TopListPresenter;
import no.ntnu.mikaelr.delta.view.signature.TopListView;
import org.json.JSONArray;

import java.util.List;

public class TopListPresenterImpl implements TopListPresenter, ProjectInteractorImpl.OnFinishedLoadingTopList {

    private TopListView view;
    private List<HighscoreUser> topList;
    private ProjectInteractor projectInteractor;

    public TopListPresenterImpl(TopListView view) {
        this.view = view;
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
        topList = HighscoreUser.fromJson(jsonArray);
        view.updateList(topList);
    }

    @Override
    public void onLoadTopListError(int errorCode) {
        // TODO: Implement
    }

}
