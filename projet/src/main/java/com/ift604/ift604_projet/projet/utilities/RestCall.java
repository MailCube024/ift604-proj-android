package com.ift604.ift604_projet.projet.utilities;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benoit on 2015-12-13.
 */
public class RestCall extends AsyncTask<String, String, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        String urlString=params[0]; // URL to call
        String result = "";
        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

            if(in != null)
                result = convertInputStreamToString(in);

        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }

        JSONObject json = null;

        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
