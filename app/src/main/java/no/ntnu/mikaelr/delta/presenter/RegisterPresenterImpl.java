package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import no.ntnu.mikaelr.delta.interactor.LoginInteractor;
import no.ntnu.mikaelr.delta.interactor.LoginInteractorImpl;
import no.ntnu.mikaelr.delta.model.User;
import no.ntnu.mikaelr.delta.presenter.signature.LoginPresenter;
import no.ntnu.mikaelr.delta.presenter.signature.RegisterPresenter;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.view.LoginActivity;
import no.ntnu.mikaelr.delta.view.MainActivity;
import no.ntnu.mikaelr.delta.view.signature.LoginView;
import no.ntnu.mikaelr.delta.view.signature.RegisterView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class RegisterPresenterImpl implements RegisterPresenter, LoginInteractorImpl.OnRegisterListener {

    private RegisterView view;
    private Activity context;
    private LoginInteractor interactor;

    public RegisterPresenterImpl(RegisterView view) {
        this.view = view;
        this.context = (Activity) view;
        this.interactor = new LoginInteractorImpl();
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void register(String username, String password, String passwordCheck, String registerCode) {

        if (username.trim().equals("")) {
            view.showMessage("Brukernavn kan ikke være tomt");
        } else if (password.trim().equals("")) {
            view.showMessage("Passord kan ikke være tomt");
        } else if (!password.equals(passwordCheck)) {
            view.showMessage("Ops! Passordene du skrev inn er ikke like.");
        } else {
            interactor.register(username, password, registerCode, this);
        }

    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onRegisterSuccess(String result) {

        User user = User.userIn(result);

        if (user != null) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("username", user.getUsername());
            context.startActivity(intent);
        }
    }

    @Override
    public void onRegisterError(int errorCode) {
        if (errorCode == HttpStatus.PRECONDITION_FAILED.value()) {
            view.showMessage("Registreringskoden er ugyldig");
        } else if (errorCode == HttpStatus.CONFLICT.value()) {
            view.showMessage("Brukernavn er opptatt");
        } else {
            view.showMessage("Sorry! Det oppsto en feil.");
        }
    }
}
