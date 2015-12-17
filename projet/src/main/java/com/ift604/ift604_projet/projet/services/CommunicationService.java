package com.ift604.ift604_projet.projet.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Pair;
import com.ift604.ift604_projet.projet.utilities.RestGet;
import com.ift604.ift604_projet.projet.utilities.RestPost;
import com.ift604.ift604_projet.projet.utilities.RestUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Benoit on 2015-12-15.
 */
public class CommunicationService extends Service {

    private final IBinder binder = new CommunicationBinder();
    private Properties communicationProperties = null;
    private boolean logged = false;

    @Override
    public IBinder onBind(Intent intent) {
        if(communicationProperties == null) {
            LoadProperties();
            Login();
        }

        return binder;
    }

    public class CommunicationBinder extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }

    private String getServerPath() {
        return "http://" + communicationProperties.getProperty("server") + ":" + communicationProperties.getProperty("port") + "/";
    }

    private void LoadProperties() {
        communicationProperties = new Properties();

        try {
            File file = new File(getFilesDir(),"config.properties");
            FileInputStream fileInput = new FileInputStream(file);
            communicationProperties.load(fileInput);
            fileInput.close();
        } catch (IOException e) {
            communicationProperties.setProperty("server", "localhost");
            communicationProperties.setProperty("port", "60631");
            communicationProperties.setProperty("username", "");
            communicationProperties.setProperty("password", "");
            communicationProperties.setProperty("email", "");
            communicationProperties.setProperty("region", "0");
            SaveProperties();
        }
    }

    private void SaveProperties() {
        try {
            File file = new File(getFilesDir(),"config.properties");
//            if(!file.exists())
//                file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            communicationProperties.store(fileOut, "");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ChangeLoginProperties(String username, String password, String email, String region) {
        communicationProperties.put("username", username);
        communicationProperties.put("password", password);
        communicationProperties.put("email", email);
        communicationProperties.put("region", region);
        SaveProperties();
        Login();
    }

    public void ChangeSettingsProperties(String server, String port) {
        communicationProperties.put("server", server);
        communicationProperties.put("port", port);
        SaveProperties();
        Login();
    }

    public boolean IsLogged() {
        return logged;
    }

    public Map<String,String> GetLoginInfo() {
        Map<String,String> m = new HashMap<>();
        m.put("username", communicationProperties.getProperty("username"));
        m.put("password", communicationProperties.getProperty("password"));
        m.put("email", communicationProperties.getProperty("email"));
        m.put("region", communicationProperties.getProperty("region"));

        return m;
    }

    public Map<String,String> GetSettings() {
        Map<String,String> m = new HashMap<>();
        m.put("server", communicationProperties.getProperty("server"));
        m.put("port", communicationProperties.getProperty("port"));

        return m;
    }

    private JSONArray Get(String url, List<Pair<String, String>> params) {
        AsyncTask<String, String, JSONArray> task = new RestGet().execute(url, RestUtil.getQueryParams(params));
        JSONArray response = null;
        try {
            response = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return response;
    }

    private JSONArray Post(String url, List<Pair<String, String>> params) {
        AsyncTask<String, String, JSONArray> task = new RestPost().execute(url, RestUtil.getQueryParams(params));
        JSONArray response = null;
        try {
            response = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return response;
    }

    public List<String> GetRanking() {
        //Get(getServerPath() + "Ranking", null)
        List<String> l = new ArrayList<>();
        l.add("Test - 32 points");
        l.add("Test2 - 30 points");
        l.add("Test3 - 15 points");
        l.add("Test4 - 1 points");
        return l;
    }

    public Map<String,String> GetRegions() {
        //Get(getServerPath()+ "Regions", null);
        Map<String,String> regions = new HashMap<>();
        regions.put("0", "Test 0");
        regions.put("1", "Test 1");

        return regions;
    }

    public void Login() {
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("Username", communicationProperties.getProperty("username")));
        params.add(new Pair<>("Password", communicationProperties.getProperty("password")));
        params.add(new Pair<>("Email", communicationProperties.getProperty("email")));
        params.add(new Pair<>("Region", communicationProperties.getProperty("region")));
        //Post(getServerPath()+"Login", params);

        logged = true;
    }
}
