package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pelicula extends AudiovisualInterface implements Serializable{

    public void setDataFromJson(ModelMovie model){
        super.TituloOriginal = model.original_title;
        super.Titulo = model.title;
        super.Id = model.id;
        super.Anyo = MyUtils.getYearFromDate(model.release_date);
        super.Image_path = model.poster_path;
        super.Rating = model.vote_average;
        super.Descripcion = model.overview == null ? "" : model.overview;
        super.originalLanguage = model.original_language;

        for(ModelGenres genres : model.genres){
            super.addGeneros(genres.name);
        }

        super.Tipo = General.MOVIE_TYPE;
        super.source = General.NET_SOURCE;
    }
}
