package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.mikaelr.delta.interactor.LoginInteractor;
import no.ntnu.mikaelr.delta.interactor.LoginInteractorImpl;
import no.ntnu.mikaelr.delta.presenter.signature.LoginPresenter;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.MainActivity;
import no.ntnu.mikaelr.delta.view.signature.LoginView;

public class LoginPresenterImpl implements LoginPresenter, LoginInteractorImpl.OnLoginListener {

    private LoginView view;
    private Activity context;
    private LoginInteractor interactor;

    public LoginPresenterImpl(LoginView view) {
        this.view = view;
        this.context = (Activity) view;
        this.interactor = new LoginInteractorImpl();
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void login(String username, String password) {
        interactor.login(username, password, this);
    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    // ASYNC TASK LISTENERS --------------------------------------------------------------------------------------------

    @Override
    public void onLoginSuccess(String cookie) {
        SharedPrefsUtil.getInstance().setCookie(cookie);
        goToMainActivity();
    }

    @Override
    public void onLoginError(int errorCode) {
        view.showMessage("Feil brukernavn eller passord");
    }
}
