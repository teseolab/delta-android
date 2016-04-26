package no.ntnu.mikaelr.delta.async_task;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import org.json.JSONArray;

public class GetProjectsAsyncTask extends JsonAsyncTask {

    private ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener;

    public GetProjectsAsyncTask(String request, ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener) {
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
