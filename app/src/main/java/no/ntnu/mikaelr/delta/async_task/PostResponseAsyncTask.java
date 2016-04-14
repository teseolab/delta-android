package no.ntnu.mikaelr.delta.async_task;

import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.StatusCode;
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

        if (result.first == StatusCode.HTTP_OK) {
            try {
                JSONObject response = new JSONObject(result.second.getBody());
                listener.onPostProjectResponseSuccess(response);
            } catch (JSONException e) {
                listener.onPostProjectResponseError(StatusCode.JSON_PARSE_EXCEPTION);
            }
        }
        else {
            listener.onPostProjectResponseError(result.first);
        }
    }

}
