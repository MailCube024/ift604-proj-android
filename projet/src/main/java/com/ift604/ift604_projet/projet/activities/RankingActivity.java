package com.ift604.ift604_projet.projet.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import com.ift604.ift604_projet.projet.R;
import com.ift604.ift604_projet.projet.services.CommunicationService;
import com.ift604.ift604_projet.projet.utilities.RestCall;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Benoit on 2015-12-14.
 */
public class RankingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        AsyncTask<String, String, JSONObject> task = new RestCall().execute("url");
        try {
            JSONObject rank = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        doBindService();
    }

    protected CommunicationService boundService;
    private boolean isServiceBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((CommunicationService.CommunicationBinder)service).getService();
            //boundService.registerClient(HockeyActivity.this);

            boundService.GetRanking();
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if(boundService != null)
//            boundService.unregisterClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(boundService != null)
//            boundService.registerClient(this);
    }

    void doBindService() {
        bindService(new Intent(RankingActivity.this, CommunicationService.class), connection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    void doUnbindService() {
        if (isServiceBound) {
            // Detach our existing connection.
            unbindService(connection);
            isServiceBound = false;
        }
    }
}
