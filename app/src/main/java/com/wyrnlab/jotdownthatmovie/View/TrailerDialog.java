package com.wyrnlab.jotdownthatmovie.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.ViewGroup;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchMovieURLTrailer;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShowURLTrailer;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Trailer;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class TrailerDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    public Context context;
    ProgressDialog pDialog;
    String originalLanguage;
    String localeLanguage;
    String selectedCode;
    AudiovisualInterface pelicula;
    public LinkedHashMap<String,String> languages = new LinkedHashMap<String, String>();;

    public TrailerDialog(Context context, String originalLanguage, String localeLanguage, AudiovisualInterface pelicula) {
        super(context);
        this.context = context;
        this.originalLanguage = originalLanguage;
        this.localeLanguage = localeLanguage;
        this.pelicula = pelicula;

        super.setTitle(context.getString(R.string.AvailableTrailers));
        super.setNegativeButton(context.getString(R.string.Cancel), null);

        super.setPositiveButton(context.getString(R.string.Search), this);
        setOptions();
    }

    private void setOptions(){
        languages.put("es", context.getString(R.string.Spanish));
        languages.put("en", context.getString(R.string.English));

        String originalLang = originalLanguage.substring(0,2);
        if(localeLanguage.equalsIgnoreCase("es")){
            languages.put(originalLang, context.getString(R.string.OriginalLanguage) + " (" + General.getLanguageTranslations(originalLang) + ")");
        } else {
            languages.put(originalLang, context.getString(R.string.OriginalLanguage) + " (" + General.getLanguageTranslations(originalLang) + ")");
        }

        selectedCode = "es";

        super.setSingleChoiceItems(languages.values().toArray(new String[0]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCode = String.valueOf(languages.keySet().toArray()[which]);
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.searching));
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ((Activity)context).findViewById(android.R.id.content)).getChildAt(0);

        // TODO: Cambiar a una interfaz
        if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
            SearchMovieURLTrailer searchorMovie = new SearchMovieURLTrailer(context, selectedCode, pelicula) {
                @Override
                public void onResponseReceived(Object result) {
                    List<Trailer> trailers = (List<Trailer>) result;
                    pDialog.dismiss();
                    if (trailers.isEmpty()) {
                        MyUtils.showSnacknar((ViewGroup) ((ViewGroup) ((Activity)context).findViewById(android.R.id.content)).getChildAt(0), context.getResources().getString(R.string.notAviableTrailer));
                    } else {
                        Intent intent = new Intent(context, YoutubeActivityView.class);
                        intent.putExtra("TrailerId", trailers.get(0).key); // TODO permitir seleccionar otros
                        ((Activity) context).startActivityForResult(intent, 1);
                    }
                }
            };
            searchorMovie.execute();
        } else {
            SearchShowURLTrailer searchorMovie = new SearchShowURLTrailer(context, selectedCode, pelicula) {
                @Override
                public void onResponseReceived(Object result) {
                    List<Trailer> trailers = (List<Trailer>) result;
                    pDialog.dismiss();
                    if (trailers.isEmpty()) {
                        MyUtils.showSnacknar((ViewGroup) ((ViewGroup) ((Activity)context).findViewById(android.R.id.content)).getChildAt(0), context.getResources().getString(R.string.notAviableTrailer));
                    } else {
                        Intent intent = new Intent(context, YoutubeActivityView.class);
                        intent.putExtra("TrailerId", trailers.get(0).key); // TODO permitir seleccionar otros
                        ((Activity) context).startActivityForResult(intent, 1);
                    }
                }
            };
            searchorMovie.execute();
        }
    }
}
