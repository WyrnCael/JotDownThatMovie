package com.wyrnlab.jotdownthatmovie.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.View.Activities.SearchActivity;

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

    public static void checkInternetConectionAndStoragePermission(Context context){
        if(!CheckInternetConection.isConnectingToInternet(context)){
            MyUtils.showSnacknar(((Activity)context).findViewById(R.id.LinearLayout1), context.getResources().getString(R.string.not_internet));

        }

        MyUtils.isStoragePermissionGranted(context);
    }

    public static void getGeneralURL(Context context){
        if(General.base_url == null){
            SearchBaseUrl searchor = new SearchBaseUrl(context) {
                @Override
                public void onResponseReceived(Object result) {

                }
            };
            MyUtils.execute(searchor);
        }
    }

    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
}
