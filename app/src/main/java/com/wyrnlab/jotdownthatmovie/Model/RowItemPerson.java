package com.wyrnlab.jotdownthatmovie.Model;

import android.content.Context;

import com.wyrnlab.jotdownthatmovie.R;

public class RowItemPerson extends RowItemInterface {

    public RowItemPerson(Context context, Object object) {

        AudiovisualInterface movie = (AudiovisualInterface) object;
    	this.id = movie.getId();
        this.imageId = movie.getImage() != null
                        ? movie.getImage()
                        : movie.getImagePath() == null
                            ? null
                            : General.base_url + "w92" +  movie.getImagePath();
        this.title = movie.getTitulo();
        this.type = movie.getTipo();
        this.desc = movie.getJob() == null ? movie.getCharacter() : movie.getJob();
        this.source = movie.getSource();
        this.object = object;
        this.context = context;
    }


}
