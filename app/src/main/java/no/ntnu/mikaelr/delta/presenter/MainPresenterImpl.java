package no.ntnu.mikaelr.delta.presenter;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.presenter.signature.MainPresenter;
import no.ntnu.mikaelr.delta.view.signature.MainView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPresenterImpl implements MainPresenter {

    public MainPresenterImpl(MainView view) {

    }

    @Override
    public List<String> getDrawerMenuItems() {
        return new ArrayList<String>(Arrays.asList("Prosjekter", "Profil", "Toppliste", "Min aktivitet", "Logg ut"));
    }

}
