package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.Project;

import java.util.ArrayList;

public interface MainView {
    void addProjectListFragment(ArrayList<Project> projects);

    void removeProjectListFragment();
}
