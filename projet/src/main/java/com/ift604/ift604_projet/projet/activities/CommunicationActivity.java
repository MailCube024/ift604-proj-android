package com.ift604.ift604_projet.projet.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.ift604.ift604_projet.projet.services.CommunicationService;

/**
 * Created by Benoit on 2015-12-16.
 */
public abstract class CommunicationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doBindService();
    }

    protected CommunicationService boundService;
    private boolean isServiceBound = false;

    protected void onServiceConnected() { }
    protected void onServiceDisconnected() { }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((CommunicationService.CommunicationBinder)service).getService();
            CommunicationActivity.this.onServiceConnected();
         }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
            CommunicationActivity.this.onServiceDisconnected();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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
}
