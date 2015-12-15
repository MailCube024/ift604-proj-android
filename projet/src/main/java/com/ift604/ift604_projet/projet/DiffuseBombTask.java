package com.ift604.ift604_projet.projet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiffuseBombTask extends AsyncTask<Double, Void, String> {
    private static final String BASE_URL = "http://localhost/%s/";
    private static final String SERVICE = "Mobile/DiffuseBomb/%1$,.2f/%1$,.2f";

    private Context mContext;
    private String mProfile;
    private Button mDiffuseButton;

    public DiffuseBombTask(Context context, String profile, Button diffuseButton) {
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
                Toast msg = null;
                JSONObject json = new JSONObject(result);
                boolean isDiffused = json.getBoolean("result");


                if (isDiffused) {
                    msg = Toast.makeText(mContext, "Bomb diffused !", Toast.LENGTH_LONG);
                    mDiffuseButton.setVisibility(View.INVISIBLE);
                    Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    mVibrator.cancel();
                } else
                    msg = Toast.makeText(mContext, "Bomb not diffused !", Toast.LENGTH_LONG);

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
