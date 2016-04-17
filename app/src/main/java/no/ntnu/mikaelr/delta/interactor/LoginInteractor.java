package no.ntnu.mikaelr.delta.interactor;

public interface LoginInteractor {

    void login(String username, String password, LoginInteractorImpl.OnLoginListener listener);

}
