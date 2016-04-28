package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;
import org.json.JSONObject;

public class ProfilePresenterImpl implements ProfilePresenter, ProjectInteractorImpl.OnGetUserListener {

    private ProfileView view;
    private ProjectInteractor interactor;

    private HighscoreUser user;

    public ProfilePresenterImpl(ProfileView view) {
        this.view = view;
        this.interactor = new ProjectInteractorImpl();
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void loadProfile() {
        interactor.getMe(this);
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onGetUserSuccess(JSONObject jsonObject) {
        user = HighscoreUser.fromJsonObject(jsonObject);
        view.updateView(user);
    }

    @Override
    public void onGetUserError(int errorCode) {
        // TODO: Handle
    }
}
