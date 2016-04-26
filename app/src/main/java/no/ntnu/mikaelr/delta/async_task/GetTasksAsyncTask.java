package no.ntnu.mikaelr.delta.async_task;

import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import org.json.JSONArray;

public class GetTasksAsyncTask extends JsonAsyncTask {

    private ProjectInteractorImpl.OnFinishedLoadingTasksListener listener;

    public GetTasksAsyncTask(String request, ProjectInteractorImpl.OnFinishedLoadingTasksListener listener) {
        super(request);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {

        JSONArray jsonArray = super.parseToJson(result);
        if (jsonArray != null) {
            listener.onFinishedLoadingTasks(jsonArray);
        }
    }

}
