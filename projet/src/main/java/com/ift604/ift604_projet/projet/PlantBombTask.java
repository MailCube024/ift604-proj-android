package com.ift604.ift604_projet.projet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlantBombTask extends AsyncTask<Double, Void, String> {
    public static String mBaseUrl;
    public static String mService;

    private Context mContext;
    private String mProfile;

    public PlantBombTask(Context context, String profile) {
        mContext = context;
        mProfile = profile;
    }

    @Override
    protected String doInBackground(Double... params) {
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            URL url = new URL(String.format(mBaseUrl, mProfile) + String.format(mService, params[0], params[1]));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = streamToString(in);

        } catch(IOException e) {
            Log.e("Erreur", "IOException");
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if(result != null) {
                Toast msg = null;
                JSONObject json = new JSONObject(result);
                boolean isPlanted = json.getBoolean("Planted");


                if (isPlanted)
                    msg = Toast.makeText(mContext, "Bomb planted !", Toast.LENGTH_LONG);
                else
                    msg = Toast.makeText(mContext, "Bomb not planted !", Toast.LENGTH_LONG);

                if (msg != null)
                    msg.show();
            }
        } catch (JSONException e) {
            Log.e("Erreur", "JSONException");
        }
    }

    private String streamToString(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String result = "";
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (IOException e) {
            Log.e("Erreur", "IOException");
        }

        return result;
    }
}
