package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.async_task.LoginAsyncTask;

public class LoginInteractorImpl implements LoginInteractor {

    public interface OnLoginListener {
        void onLoginSuccess();
        void onLoginError(int errorCode);
    }

    public void login(String username, String password, OnLoginListener listener) {
        String apiCall = "http://129.241.102.204:8080/login";
        new LoginAsyncTask(apiCall, username, password, listener).execute();
    }

}
