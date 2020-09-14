package com.wyrnlab.jotdownthatmovie.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.wyrnlab.jotdownthatmovie.Model.Trailer;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;

import java.util.ArrayList;
import java.util.List;

public class TrailerDialog extends AlertDialog.Builder {
    Context context;
    List<Trailer> trailers;

    public TrailerDialog(Context context, List<Trailer> trailers) {
        super(context);
        this.context = context;
        this.trailers = trailers;

        super.setTitle(context.getString(R.string.AvailableTrailers));
        setOptions();
    }

    private void setOptions(){
        List<String> options = new ArrayList<String>();
        for(Trailer trailer : trailers) {
            options.add(trailer.name + " (" + trailer.iso_639_1 + ")");
        }

        super.setSingleChoiceItems(options.toArray(new String[0]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, YoutubeActivityView.class);
                intent.putExtra("TrailerId", trailers.get(which).key);
                ((Activity)context).startActivityForResult(intent, 1);
            }
        });
    }
}
