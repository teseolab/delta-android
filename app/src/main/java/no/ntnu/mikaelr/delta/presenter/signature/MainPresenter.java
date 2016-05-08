package no.ntnu.mikaelr.delta.presenter.signature;

import no.ntnu.mikaelr.delta.model.Project;

import java.util.List;

public interface MainPresenter {
    void loadProjects();
    List<Project> getProjects();
}
