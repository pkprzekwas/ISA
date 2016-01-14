package me.isassist.isa;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;

public class FetchDataService extends IntentService {

    private final String TAG = this.getClass().getSimpleName();

    public FetchDataService() {
        super("FetchDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        for (Bihapi api : Bihapi.values())
        {
            String json = fetchJSON(api);
            ArrayList<Hashtable<String, String>> data;
            try {
                data = JSONParser.getDataFromJSON(json);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                continue;
            }
            saveToFile(api, data);
        }
    }

    private boolean saveToFile(Bihapi api, ArrayList<Hashtable<String, String>> data)
    {
        Log.d(TAG, "Starting saving to file " + api.name());
        try {
            FileOutputStream fileOutputStream = this.openFileOutput(api.name(), Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
            fileOutputStream.close();
            Log.d(TAG, "Saved " + api.name());
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private String fetchJSON(Bihapi api)
    {
        // Will contain the raw JSON response as a string.
        String JSONString = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        Authenticator.setDefault(new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.API_LOGIN, Constants.API_PASSWORD.toCharArray());
            }
        });

        Uri.Builder uriBuilder = Uri.parse(api.getURL()).buildUpon();
        Uri uri = uriBuilder.build();

        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(new CustomCACert(this, R.raw.ca_tp_der).getSSLSocketFactory());
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                return null;
            }
            JSONString = buffer.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return JSONString;
    }
}
