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

            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/601.7.7 (KHTML, like Gecko) Version/9.1.2 Safari/601.7.7").header("Accept-Language", "en").ignoreContentType(true).followRedirects(true).get();
            List<Streaming> streamingList = new ArrayList<Streaming>();
            if(doc != null && doc.select("div.ott_provider") != null) {
                for (Element divProviders : doc.select("div.ott_provider")) {
                    for (Element li : divProviders.select("li:not(.hide)")) {
                        Streaming strObject = new Streaming(li);
                        streamingList.add(strObject);
                    }
                }
            }


            return streamingList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

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
