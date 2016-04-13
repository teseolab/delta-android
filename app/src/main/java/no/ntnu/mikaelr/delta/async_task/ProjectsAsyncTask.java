package no.ntnu.mikaelr.delta.async_task;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import org.json.JSONArray;

public class ProjectsAsyncTask extends JsonAsyncTask {

    private ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener;

    public ProjectsAsyncTask(String request, ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener) {
        super(request);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {

        JSONArray jsonArray = super.parseToJson(result);
        if (jsonArray != null) {
            listener.onFinishedLoadingProjects(jsonArray);
        }
    }

}
