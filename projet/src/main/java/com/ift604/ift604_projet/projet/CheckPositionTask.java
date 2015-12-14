package com.ift604.ift604_projet.projet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckPositionTask  extends AsyncTask<Double, Void, String> {
    private static final String BASE_URL = "http://localhost/%s/";
    private static final String SERVICE = "Mobile/CheckPosition/%1$,.2f/%1$,.2f";
    private static final long[] closePattern = { 0, 200, 1000 };
    private static final long[] veryClosePattern = { 0, 200, 500 };
    private static final long[] diffusePattern = { 0, 50, 0 };

    private Context mContext;
    private String mProfile;
    private Button mDiffuseButton;

    public CheckPositionTask(Context context, String profile, Button diffuseButton) {
        mContext = context;
        mProfile = profile;
        mDiffuseButton = diffuseButton;
    }

    @Override
    protected String doInBackground(Double... params) {
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            URL url = new URL(String.format(BASE_URL, mProfile) + String.format(SERVICE, params[0], params[1]));
            urlConnection = (HttpURLConnection) url.openConnection();

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
                JSONObject json = new JSONObject(result);
                Double distance = json.getDouble("distance");

                Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

                if (distance < 100)
                    mVibrator.vibrate(closePattern, 0);
                else if (distance < 50)
                    mVibrator.vibrate(veryClosePattern, 0);
                else if (distance < 10) {
                    mVibrator.vibrate(diffusePattern, 0);
                    mDiffuseButton.setVisibility(View.VISIBLE);
                }
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
