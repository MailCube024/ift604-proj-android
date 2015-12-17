package com.ift604.ift604_projet.projet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class ClosestDistanceTask extends AsyncTask<Double, Void, String> {
    private static final long[] closePattern = { 0, 200, 1000 };
    private static final long[] veryClosePattern = { 0, 200, 500 };
    private static final long[] defusePattern = { 0, 50, 0 };

    public static String mBaseUrl;
    public static String mService;

    private Context mContext;
    private Button mDefuseButton;
    private LocalBroadcastManager mBroadcaster;
    public String mUsername;

    public ClosestDistanceTask(Context context, Button defuseButton, String username) {
        mContext = context;
        mDefuseButton = defuseButton;
        mUsername = username;
        mBroadcaster = LocalBroadcastManager.getInstance(context);
    }

    @Override
    protected String doInBackground(Double... params) {
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            URL url = new URL(mBaseUrl + String.format(Locale.US, mService, params[0], params[1], mUsername));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

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
                Integer bombId = json.getInt("BombId");

                if(bombId != -1) {
                    Intent intent = new Intent("Distance");
                    intent.putExtra("BombId", bombId);

                    mBroadcaster.sendBroadcast(intent);

                    Double distance = json.getDouble("Distance");

                    Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

                    if (distance < 10) {
                        mVibrator.vibrate(defusePattern, 0);
                        mDefuseButton.setVisibility(View.VISIBLE);
                    } else if (distance < 50)
                        mVibrator.vibrate(veryClosePattern, 0);
                    else if (distance < 100)
                        mVibrator.vibrate(closePattern, 0);
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
            while ((line = bufferedReader.readLine()) != null)
                result += line + "\n";
        } catch (IOException e) {
            Log.e("Erreur", "IOException");
        }

        return result;
    }
}
