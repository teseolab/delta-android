package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.util.Pair;
import no.ntnu.mikaelr.delta.interactor.LoginInteractorImpl;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class PostAgreementAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity<String>>> {

    private String request;
    private ProjectInteractorImpl.OnPostAgreementListener listener;

    public PostAgreementAsyncTask(String request, ProjectInteractorImpl.OnPostAgreementListener listener) {
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected Pair<Integer, ResponseEntity<String>> doInBackground(Void... params) {

        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", SharedPrefsUtil.getInstance().getCookie());

        HttpEntity<String> entity = new HttpEntity<String>(headers);

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
            Integer numberOfAgreements = Integer.valueOf(result.second.getHeaders().get("agreements").get(0));
            Integer numberOfDisagreements = Integer.valueOf(result.second.getHeaders().get("disagreements").get(0));
            listener.onPostAgreementSuccess(numberOfAgreements, numberOfDisagreements);
        } else {
            listener.onPostAgreementError(result.first);
        }
    }

}
