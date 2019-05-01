package com.wyrnlab.jotdownthatmovie.Analytics;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



import com.wyrnlab.jotdownthatmovie.Utils.Http;
import com.wyrnlab.jotdownthatmovie.api.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.api.search.Movies.Pelicula;
import com.wyrnlab.jotdownthatmovie.data.General;
import com.wyrnlab.jotdownthatmovie.data.SetTheLanguages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jota on 27/12/2017.
 */

public class OpenApp extends AsyncTask<String, Integer, String> {

    private HttpURLConnection yc;
    Context context;

    public OpenApp(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public void sendNewConnection() {
        String web = null;
        InputStream is = null;

        try {
            JSONObject json = new JSONObject();
            json.put("token", General.ANALYTICS_APIKEY);
            json.put("datetime", System.currentTimeMillis());
            json.put("model", Build.MODEL);

            @SuppressWarnings("deprecation")
            String url = "http://wirncael.duckdns.org:5151/app_open" ;
            URL oracle = new URL(url);

            yc = (HttpURLConnection) oracle.openConnection();


            //yc.setDoOutput(true);
            yc.setDoInput(true);
            yc.setInstanceFollowRedirects(false);
            yc.setRequestMethod("POST");
            //yc.setUseCaches (false);
            yc.setRequestProperty("Content-Type", "application/json");
            yc.setConnectTimeout(4000);

            Http.setPostRequestContent(yc, json);

            yc.connect();
            is = yc.getInputStream();

        } catch (IOException | JSONException ioe) {

        }
        yc.disconnect();
    }

    @Override
    protected String doInBackground(String... params) {
        sendNewConnection();

        return null;

    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
    }
}

