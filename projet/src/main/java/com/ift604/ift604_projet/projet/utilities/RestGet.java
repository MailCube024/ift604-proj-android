package com.ift604.ift604_projet.projet.utilities;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benoit on 2015-12-13.
 */
public class RestGet extends AsyncTask<String, String, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... params) {
        String urlString=params[0]; // URL to call
        String par = params[1];
        if(par != null && par != "")
            urlString += "?" + par;

        String result = "";
        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //Object test = urlConnection.getContent();
            in = new BufferedInputStream(urlConnection.getInputStream());

            if(in != null)
                result = RestUtil.convertInputStreamToString(in);

        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }

        return RestUtil.parseJSON(result);
    }
}
