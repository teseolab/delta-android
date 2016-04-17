package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import android.util.Pair;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class DisagreementAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity<String>>> {

    private String request;
    private ProjectInteractorImpl.OnPostDisagreementListener listener;

    public DisagreementAsyncTask(String request, ProjectInteractorImpl.OnPostDisagreementListener listener) {
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected Pair<Integer, ResponseEntity<String>> doInBackground(Void... params) {

        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);

        try {
            ResponseEntity<String> result = template.exchange(request, HttpMethod.POST, null, String.class);
            return new Pair<Integer, ResponseEntity<String>>(StatusCode.HTTP_OK, result);
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
            listener.onPostDisagreementSuccess(numberOfAgreements, numberOfDisagreements);
        } else {
            listener.onPostDisagreementError(result.first);
        }
    }

}
