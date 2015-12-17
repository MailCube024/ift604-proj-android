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
import org.json.JSONException;
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
            communicationProperties.setProperty("region", "0");
            SaveProperties();
        }
    }

    private void SaveProperties() {
        try {
            File file = new File(getFilesDir(),"config.properties");
            FileOutputStream fileOut = new FileOutputStream(file);
            communicationProperties.store(fileOut, "");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ChangeLoginProperties(String username, String password) {
        communicationProperties.put("username", username);
        communicationProperties.put("password", password);
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
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("username", communicationProperties.getProperty("username")));

        List<String> l = new ArrayList<>();

        try {
            JSONArray a = Get(getServerPath() + "Rankings/List", params);
            for(int i = 0; i < a.length(); ++i) {
                JSONObject o = a.getJSONObject(i);
                l.add(i + " - " + o.getString("Name") + " - " + o.getString("Score") + " points");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return l;
    }

    public Map<String,String> GetRegions() {
        Map<String,String> regions = new HashMap<>();

        try {
            JSONArray test = Get(getServerPath()+ "Region/List", null);
            for(int i = 0; i < test.length(); ++i) {
                JSONObject o = test.getJSONObject(i);
                regions.put(o.getString("id"), o.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return regions;
    }

    public boolean Login() {
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("username", communicationProperties.getProperty("username")));
        params.add(new Pair<>("password", communicationProperties.getProperty("password")));

        logged = false;
        try {
            JSONArray a = Post(getServerPath()+"Account/MLogin", params);
            logged = a.getBoolean(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return logged;
    }

    public Map<String, String> Register(String username, String password, String password2, String email, String region) {
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("username", username));
        params.add(new Pair<>("password", password));
        params.add(new Pair<>("confirmPassword", password2));
        params.add(new Pair<>("email", email));
        params.add(new Pair<>("regionId", region));

        Map<String,String> m = new HashMap<>();
        logged = false;
        try {
            JSONArray a = Post(getServerPath()+"Account/MRegister", params);
            logged = !Boolean.class.isInstance(a.get(0));
            if(logged) {
                JSONObject test = a.getJSONObject(0);
                m.put("username", test.getString("Username"));
                //m.put("email", test.getString("Email"));
                m.put("region", test.getString("RegionId"));
                logged = true;

                communicationProperties.put("username", username);
                communicationProperties.put("password", password);
                communicationProperties.put("region", region);
                SaveProperties();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return m;
    }
}
