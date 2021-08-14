package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCast;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.io.Serializable;

public class Cast extends AudiovisualInterface implements Serializable {

    public void setDataFromJson(ModelCast cast){
        super.TituloOriginal = cast.original_title;
        super.Titulo = cast.title;
        super.Id = cast.id;
        super.Anyo = MyUtils.getYearFromDate(cast.release_date);
        super.Image_path = cast.poster_path;
        //super.Rating = model.vote_average;
        super.Descripcion = cast.overview == null ? "" : cast.overview;
        super.originalLanguage = cast.original_language;
        super.popularity = cast.popularity;
        super.Rating = cast.vote_average;
        super.relatedToPerson = General.CAST_TYPE;

        /*for(ModelGenres genres : model.genres){
            super.addGeneros(genres.name);
        }*/

        super.Tipo = General.MOVIE_TYPE;
        super.source = General.NET_SOURCE;
        super.character = cast.character;
    }
}