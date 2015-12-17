package com.ift604.ift604_projet.projet.utilities;

import android.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Benoit on 2015-12-15.
 */
public class RestUtil {

    public static String getQueryParams(List<Pair<String,String>> params)
    {
        if(params == null)
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;

        try {
            for (Pair<String,String> pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.first, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.second, "UTF-8"));
            }

            return result.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static JSONArray parseJSON(String jsonString) {
        if(jsonString.equals(null) || jsonString.equals("")) {
            return new JSONArray();
        } else if(jsonString.startsWith("[")) {
            JSONArray json = null;

            try {
                json = new JSONArray(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        } else if(jsonString.startsWith("{")) {
            JSONArray json = new JSONArray();
            JSONObject obj = null;

            try {
                obj = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            json.put(obj);
            return json;
        } else if(Boolean.toString(true).equals(jsonString) || Boolean.toString(false).equals(jsonString)) {
            JSONArray a = new JSONArray();
            a.put(Boolean.parseBoolean(jsonString));
            return a;
        } else {
            return new JSONArray();
        }
    }
}
