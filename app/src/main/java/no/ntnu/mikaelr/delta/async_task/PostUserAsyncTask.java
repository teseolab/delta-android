package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.LoginInteractorImpl;
import no.ntnu.mikaelr.delta.model.User;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class PostUserAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity<String>>> {

    private LoginInteractorImpl.OnRegisterListener listener;

    private String request;
    private String username;
    private String password;
    private String registerCode;

    public PostUserAsyncTask(String request, String username, String password, String registerCode, LoginInteractorImpl.OnRegisterListener listener) {
        this.request = request;
        this.username = username;
        this.password = password;
        this.registerCode = registerCode;
        this.listener = listener;
    }

    @Override
    protected Pair<Integer, ResponseEntity<String>> doInBackground(Void... params) {

        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = User.userOut(username, password, registerCode);

        HttpEntity<?> entity = new HttpEntity<Object>(body, headers);

        try {
            ResponseEntity<String> response = template.exchange(request, HttpMethod.POST, entity, String.class);
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

    @Override
    protected void onPostExecute(Pair<Integer, ResponseEntity<String>> result) {
        if (result.first == StatusCode.HTTP_OK) {
            listener.onRegisterSuccess(result.second.getBody());
        } else {
            listener.onRegisterError(result.first);
        }
    }

}
