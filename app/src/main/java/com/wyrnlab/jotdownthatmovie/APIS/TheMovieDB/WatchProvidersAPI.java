package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.WatchProviders.ModelWatchProvidersResponse;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.io.IOException;

public class WatchProvidersAPI extends AsyncTask<String, Integer, ModelWatchProvidersResponse> implements ICallback {

    Context context;
    ProgressDialog pDialog;
    String id;
    String type;
    String dialogText;

    public WatchProvidersAPI(Context context, String mediaId, String type, String dialogText) {
        this.id = mediaId;
        this.type = type;
        this.dialogText = dialogText;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(this.dialogText);
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    @Override
    protected ModelWatchProvidersResponse doInBackground(String... strings) {
        String mediaPath = this.type.equalsIgnoreCase(General.MOVIE_TYPE) ? "movie" : "tv";
        String url = General.URLPRINCIPAL + "3/" + mediaPath + "/" + this.id + "/watch/providers?api_key=" + General.APIKEY;

        try {
            String json = MyUtils.getHttpRequest(url);
            return new Gson().fromJson(json, ModelWatchProvidersResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ModelWatchProvidersResponse result) {
        super.onPostExecute(result);
        pDialog.dismiss();
        onResponseReceived(result);
    }

    @Override
    public void onResponseReceived(Object result) {

    }
}
