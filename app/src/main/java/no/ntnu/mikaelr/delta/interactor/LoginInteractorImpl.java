package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.async_task.LoginAsyncTask;
import no.ntnu.mikaelr.delta.async_task.PostImageAsyncTask;
import no.ntnu.mikaelr.delta.async_task.PostUserAsyncTask;

public class LoginInteractorImpl implements LoginInteractor {

    public interface OnLoginListener {
        void onLoginSuccess(String cookie);
        void onLoginError(int errorCode);
    }

    public void login(String username, String password, OnLoginListener listener) {
        String apiCall = "http://129.241.102.204:8080/login";
        new LoginAsyncTask(apiCall, username, password, listener).execute();
    }

    public interface OnRegisterListener {
        void onRegisterSuccess(String jsonUser);
        void onRegisterError(int errorCode);
    }

    public void register(String username, String password, String registerCode, OnRegisterListener listener) {
        String apiCall = "http://129.241.102.204:8080/users";
        new PostUserAsyncTask(apiCall, username, password, registerCode, listener).execute();
    }

}
