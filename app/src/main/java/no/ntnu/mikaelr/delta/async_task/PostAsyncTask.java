package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.*;

public abstract class PostAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity<String>>> {

    private String request;
    private String body;

    public PostAsyncTask(String request, String body) {
        this.request = request;
        this.body = body;
    }

    @Override
    protected Pair<Integer, ResponseEntity<String>> doInBackground(Void... params) {

        ResponseEntity<String> response;
        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json; charset=utf-8");
        headers.set("Cookie", null); //TODO: Set cookie
        HttpEntity<String> entity = new HttpEntity<String>(body, headers);

        try {
            response = template.exchange(request, HttpMethod.POST, entity, String.class);
            return new Pair<Integer, ResponseEntity<String>>(StatusCode.HTTP_OK, response);
        }

        catch (HttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity<String>>(e.getStatusCode().value(), null);
        }

        catch (ResourceAccessException e) {
            return new Pair<Integer, ResponseEntity<String>>(StatusCode.NETWORK_UNREACHABLE, null);
        }

        catch (UnknownHttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity<String>>(StatusCode.HTTP_UNKNOWN, null);
        }
    }

}
