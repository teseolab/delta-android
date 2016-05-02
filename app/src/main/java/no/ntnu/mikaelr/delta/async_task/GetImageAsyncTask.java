package no.ntnu.mikaelr.delta.async_task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.util.SharedPrefsUtil;
import no.ntnu.mikaelr.delta.util.StatusCode;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

public class GetImageAsyncTask extends AsyncTask<Void, Void, Pair<Integer, ResponseEntity<byte[]>>> {

    private ProjectInteractorImpl.OnGetImageListener listener;

    private String request;

    public GetImageAsyncTask(String request, ProjectInteractorImpl.OnGetImageListener listener) {
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected Pair<Integer, ResponseEntity<byte[]>> doInBackground(Void... params) {

        RestTemplate template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 10);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", SharedPrefsUtil.getInstance().getCookie());
        HttpEntity<?> entity = new HttpEntity<Object>(headers);

        try {
            ResponseEntity<byte[]> response = template.exchange(request, HttpMethod.GET, entity, byte[].class);
            return new Pair<Integer, ResponseEntity<byte[]>>(StatusCode.HTTP_OK, response);
        }

        catch (HttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity<byte[]>>(e.getStatusCode().value(), null);
        }

        catch (ResourceAccessException e) {
            return new Pair<Integer, ResponseEntity<byte[]>>(StatusCode.NETWORK_UNREACHABLE, null);
        }

        catch (UnknownHttpStatusCodeException e) {
            return new Pair<Integer, ResponseEntity<byte[]>>(StatusCode.HTTP_UNKNOWN, null);
        }
    }

    @Override
    protected void onPostExecute(Pair<Integer, ResponseEntity<byte[]>> result) {
        if (result.first == StatusCode.HTTP_OK) {
            byte[] byteArray = result.second.getBody();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);
            if (bitmap != null) {
                listener.onGetImageSuccess(bitmap);
            } else {
                listener.onGetImageError(StatusCode.IMAGE_DECODE_FAILED);
            }
        } else {
            listener.onGetImageError(result.first);
        }
    }

}
