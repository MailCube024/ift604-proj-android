package com.ift604.ift604_projet.projet.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GameEventsService extends Service {
    private Timer timer;
    private LocalBroadcastManager mBroadcaster;

    private CommunicationService boundService;
    private boolean isServiceBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((CommunicationService.CommunicationBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, CommunicationService.class), connection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    void doUnbindService() {
        if (isServiceBound) {
            // Detach our existing connection.
            unbindService(connection);
            isServiceBound = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GameEvents", "Service started !");
        mBroadcaster = LocalBroadcastManager.getInstance(this);

        doBindService();

        TimerTask task = new TimerTask() {
            public void run() {
                Integer state = boundService.GetEventState();
                if(state != -1) {
                    Log.d("GameEvents", "Received !");
                    Intent intent = new Intent("GameEvents");
                    intent.putExtra("State", state);
                    mBroadcaster.sendBroadcast(intent);
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 10 * 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doUnbindService();
        timer.cancel();
        Log.d("GameEvents", "Service stopped !");
    }
}
