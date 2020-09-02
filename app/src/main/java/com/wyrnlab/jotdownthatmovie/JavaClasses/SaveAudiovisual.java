package com.wyrnlab.jotdownthatmovie.JavaClasses;

import android.app.Activity;
import android.content.Context;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchInfoShow;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.SearchResultActivity;

public class SaveAudiovisual {
    public static void saveItem(Context context, AsyncResponse asyncResponse, AudiovisualInterface item, String type){
        if(type.equalsIgnoreCase("Movie")) {
            SearchInfoMovie searchorMovie = new SearchInfoMovie(context, item.getId(), context.getString(R.string.saving));
            //searchorMovie.position = item;
            searchorMovie.delegate = asyncResponse;
            MyUtils.execute(searchorMovie);

        } else {
            SearchInfoShow searchorShow = new SearchInfoShow(context, item.getId(), context.getString(R.string.saving));
            //searchorShow.position = item;
            searchorShow.delegate = asyncResponse;
            MyUtils.execute(searchorShow);
        }
    }
}
