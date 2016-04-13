package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.async_task.ProjectsAsyncTask;
import no.ntnu.mikaelr.delta.async_task.TasksAsyncTask;
import org.json.JSONArray;

public class ProjectInteractorImpl implements ProjectInteractor {

    public interface OnFinishedLoadingProjectsListener {
        void onFinishedLoadingProjects(JSONArray jsonArray);
    }

    @Override
    public void getProjects(OnFinishedLoadingProjectsListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects";
        new ProjectsAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingTasksListener {
        void onFinishedLoadingTasks(JSONArray jsonArray);
    }

    @Override
    public void getTasks(int projectId, OnFinishedLoadingTasksListener listener) {
        int incrementedProjectId = projectId + 1;
        String apiCall = "http://129.241.102.204:8080/projects/" + incrementedProjectId + "/tasks";
        new TasksAsyncTask(apiCall, listener).execute();
    }
}
