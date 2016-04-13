package no.ntnu.mikaelr.delta.async_task;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class JsonAsyncTask extends AsyncTask<Void, Void, String> {

    private String request;

    public JsonAsyncTask(String request) {
        this.request = request;
    }

    @Override
    protected String doInBackground(Void... params) {

        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(request);
            connection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // TODO: Handle exceptions
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        return result.toString();
    }

    protected JSONArray parseToJson(String result) {
        JSONArray json;
        try {
            json = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

}
