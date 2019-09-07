package com.wyrnlab.jotdownthatmovie.Utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyUtils {

    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    public static void showSnacknar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .show();
    }

    public static String getYearFromDate(String date){
        if (date != null) {
            // Recortar año
            String anyo;
            if (date.length() > 0) {
                anyo = date.substring(0, 4);
            } else {
                anyo = "N/D";
            }
            return anyo;
        } else {
            return "N/D";
        }
    }

    public static String getHttpRequest(String url) throws IOException{
        HttpsURLConnection yc;
        URL oracle = new URL(url);
        yc = (HttpsURLConnection) oracle.openConnection();

        yc.setDoInput(true);
        yc.setInstanceFollowRedirects(false);
        yc.setRequestMethod("GET");
        yc.setRequestProperty("Accept", "application/json");

        yc.connect();

        InputStream is = null;
        try {
            is = yc.getInputStream();
        } catch (IOException ioe) {
            if (yc instanceof HttpsURLConnection) {
                HttpsURLConnection httpConn = (HttpsURLConnection) yc;
                int statusCode = httpConn.getResponseCode();
                if (statusCode != 200) {
                    is = httpConn.getErrorStream();
                }
            }
        }

        InputStreamReader isReader = new InputStreamReader(is);
        //put output stream into a string
        BufferedReader br = new BufferedReader(isReader );
        String inputLine;
        String response = "";
        while ((inputLine = br.readLine()) != null)
            response += inputLine;
        br.close();
        yc.disconnect();

        return response;
    }
}
