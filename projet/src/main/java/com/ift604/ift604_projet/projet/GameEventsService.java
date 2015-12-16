package com.ift604.ift604_projet.projet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GameEventsService extends Service {
    private static final String BASE_URL = "http://localhost:60631/";
    private static final String EVENTS_SERVICE = "GameEvents/State";

    private Timer timer;
    private LocalBroadcastManager mBroadcaster;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GameEvents", "Service started !");
        mBroadcaster = LocalBroadcastManager.getInstance(this);

        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    HttpURLConnection urlConnection = null;
                    String result = null;

                    URL url = new URL(BASE_URL + EVENTS_SERVICE);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    result = streamToString(in);

                    if (result != null) {
                        JSONObject json = new JSONObject(result);
                        String gameEvents = json.getString("State");
                        Log.d("GameEvents", "Received !");
                        Intent intent = new Intent("State");
                        intent.putExtra("GameEvents", gameEvents);
                        mBroadcaster.sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    Log.e("Erreur", "JSONException");
                } catch (IOException e) {
                    Log.e("Erreur", "IOException");
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 10 * 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        timer.cancel();
        Log.d("GameEvents", "Service stopped !");
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
