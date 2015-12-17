package com.ift604.ift604_projet.projet.utilities;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benoit on 2015-12-15.
 */
public class RestPost extends AsyncTask<String, String, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... params) {
        String urlString=params[0]; // URL to call
        String par = params[1];

        String result = "";
        InputStream in = null;
        OutputStream os = null;

        // HTTP Post
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if(par != null && par != "") {
                os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(par);
                writer.flush();
                writer.close();
                os.close();
                os = null;
            }

            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(urlConnection.getInputStream());
                if(in != null)
                    result = RestUtil.convertInputStreamToString(in);
            }
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(os != null)
                    os.close();
                if(in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return RestUtil.parseJSON(result);
    }
}
