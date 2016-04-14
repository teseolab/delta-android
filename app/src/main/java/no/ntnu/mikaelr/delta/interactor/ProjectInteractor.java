package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.model.ProjectResponse;

public interface ProjectInteractor {

    void getProjects(ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener);
    void getTasks(int projectId, ProjectInteractorImpl.OnFinishedLoadingTasksListener listener);

    void postResponse(ProjectResponse response, ProjectInteractorImpl.OnPostProjectResponseListener listener);
}
