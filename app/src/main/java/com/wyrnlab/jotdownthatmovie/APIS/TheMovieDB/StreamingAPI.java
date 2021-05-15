package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamingAPI  extends AsyncTask<String, Integer, List<Streaming>> implements ICallback {

    public AsyncResponse delegate = null;
    Context context;
    ProgressDialog pDialog;
    String id;
    String type;
    String dialogText;

    public StreamingAPI(Context context, String mediaId, String type, String dialogText){
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
    protected List<Streaming> doInBackground(String... strings) {
        String url = this.type.equalsIgnoreCase(General.MOVIE_TYPE) ? "https://www.themoviedb.org/movie/" : "https://www.themoviedb.org/tv/";
        url += this.id + "/watch";

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Streaming> streamingList = new ArrayList<Streaming>();

        for (Element divProviders : doc.select("div.ott_provider")) {
            for(Element li : divProviders.select("li:not(.hide)")) {
                Streaming strObject = new Streaming(li);
                streamingList.add(strObject);
            }
        }


        return streamingList;
    }

    @Override
    protected void onPostExecute(List<Streaming> result)
    {
        super.onPostExecute(result);
        pDialog.dismiss();
        if(delegate != null){
            delegate.processFinish(result);
        }
        onResponseReceived(result);
    }

    @Override
    public void onResponseReceived(Object result) {

    }
}
