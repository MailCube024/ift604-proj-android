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

public class DefuseBombTask extends AsyncTask<Integer, Void, String> {
    public static String mBaseUrl;
    public static String mService;

    private Context mContext;
    private Button mDefuseButton;
    private String mUsername;

    public DefuseBombTask(Context context, Button defuseButton, String username) {
        mContext = context;
        mDefuseButton = defuseButton;
        mUsername = username;
    }

    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            URL url = new URL(mBaseUrl + String.format(mService, params[0], mUsername));
            urlConnection = (HttpURLConnection) url.openConnection();
            // Use POST method
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty( "Content-Type", "application/json");
            urlConnection.setRequestProperty( "Content-Length", new Integer(0).toString());

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
                boolean isDefused = json.getBoolean("Defused");

                if (isDefused) {
                    msg = Toast.makeText(mContext, "Bomb defused !", Toast.LENGTH_LONG);
                    mDefuseButton.setVisibility(View.INVISIBLE);
                    Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    mVibrator.cancel();
                } else
                    msg = Toast.makeText(mContext, "Bomb not defused !", Toast.LENGTH_LONG);

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
