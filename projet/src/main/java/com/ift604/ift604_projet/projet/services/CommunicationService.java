package com.ift604.ift604_projet.projet.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.ift604.ift604_projet.projet.utilities.RestCall;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Benoit on 2015-12-15.
 */
public class CommunicationService extends Service {

    private final IBinder binder = new CommunicationBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class CommunicationBinder extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }

    private JSONObject Get(String url) {
        AsyncTask<String, String, JSONObject> task = new RestCall().execute(url);
        JSONObject response = null;
        try {
            response = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return response;
    }

    public JSONObject GetRanking() {
        return Get("url");
    }

    public boolean Login() {
        return Get("url") != null;
    }
}
