package no.ntnu.mikaelr.delta.async_task;

import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class PostResponseAsyncTask extends PostAsyncTask {

    private ProjectInteractorImpl.OnPostProjectResponseListener listener;

    public PostResponseAsyncTask(String request, String body, ProjectInteractorImpl.OnPostProjectResponseListener listener) {
        super(request, body);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Pair<Integer, ResponseEntity<String>> result) {

        if (result.first == 0) { //TODO: Response success code
            try {
                JSONObject response = new JSONObject(result.second.getBody());
                listener.onPostProjectResponseSuccess(response);
            } catch (JSONException e) {
                listener.onPostProjectResponseError(123); //TODO: Error code
            }
        }
        else {
            listener.onPostProjectResponseError(123); //TODO: Error code
        }
    }

}
