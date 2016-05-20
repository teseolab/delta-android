package no.ntnu.mikaelr.delta.presenter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.TopListPresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.ProfileActivity;
import no.ntnu.mikaelr.delta.view.ProfileFragment;
import no.ntnu.mikaelr.delta.view.signature.TopListView;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.List;

public class TopListPresenterImpl implements TopListPresenter, ProjectInteractorImpl.OnFinishedLoadingTopList {

    private TopListView view;
    private FragmentActivity context;
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

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user", topList.get(position));
        context.startActivity(intent);

//        Fragment profileFragment = new ProfileFragment();
//        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.content_frame, profileFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
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
