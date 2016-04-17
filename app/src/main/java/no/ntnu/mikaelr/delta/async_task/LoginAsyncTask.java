package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.LoginInteractorImpl;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.security.Principal;
import java.util.List;

public class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {

    private LoginInteractorImpl.OnLoginListener listener;

    private String request;
    private String username;
    private String password;

    public LoginAsyncTask(String request, String username, String password, LoginInteractorImpl.OnLoginListener listener) {
        this.request = request;
        this.username = username;
        this.password = password;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", username);
        body.add("password", password);

        HttpEntity<?> entity = new HttpEntity<Object>(body, headers);

        try {
            template.exchange(request, HttpMethod.POST, entity, String.class);
            return StatusCode.HTTP_OK;
        }

        catch (HttpStatusCodeException e) {
            return e.getStatusCode().value();
        }

        catch (ResourceAccessException e) {
            return StatusCode.NETWORK_UNREACHABLE;
        }

        catch (UnknownHttpStatusCodeException e) {
            return StatusCode.HTTP_UNKNOWN;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == StatusCode.HTTP_OK) {
            listener.onLoginSuccess();
        } else {
            listener.onLoginError(result);
        }
    }

}
