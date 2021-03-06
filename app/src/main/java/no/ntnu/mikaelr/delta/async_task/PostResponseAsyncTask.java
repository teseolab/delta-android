package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class PostResponseAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity>>{

    private String request;
    private String body;

    private ProjectInteractorImpl.OnPostProjectResponseListener listener;

    public PostResponseAsyncTask(String request, String body, ProjectInteractorImpl.OnPostProjectResponseListener listener) {
        this.request = request;
        this.body = body;
        this.listener = listener;
    }

    @Override
    protected Pair<Integer, ResponseEntity> doInBackground(Void... params) {

        ResponseEntity<String> response;
        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json; charset=utf-8");
        headers.set("Cookie", SharedPrefsUtil.getInstance().getCookie());
        HttpEntity<String> entity = new HttpEntity<String>(body, headers);

        try {
            response = template.exchange(request, HttpMethod.POST, entity, String.class);
            return new Pair<Integer, ResponseEntity>(StatusCode.HTTP_OK, response);
        }

        catch (HttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity>(e.getStatusCode().value(), null);
        }

        catch (ResourceAccessException e) {
            return new Pair<Integer, ResponseEntity>(StatusCode.NETWORK_UNREACHABLE, null);
        }

        catch (UnknownHttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity>(StatusCode.HTTP_UNKNOWN, null);
        }
    }

    @Override
    protected void onPostExecute(Pair<Integer, ResponseEntity> result) {

        if (result.first == StatusCode.HTTP_OK) {
            listener.onPostProjectResponseSuccess();
        }
        else {
            listener.onPostProjectResponseError(result.first);
        }
    }

}
