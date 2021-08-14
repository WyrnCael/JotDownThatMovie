package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelCrewShow;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CrewShow extends AudiovisualInterface implements Serializable{

    public void setDataFromJson(ModelCrewShow model){
        super.TituloOriginal = model.original_name;
        super.Titulo = model.name;
        super.Id = model.id;
        super.Anyo = MyUtils.getYearFromDate(model.release_date);
        super.Image_path = model.poster_path;
        //super.Rating = model.vote_average;
        super.Descripcion = model.overview == null ? "" : model.overview;
        super.originalLanguage = model.original_language;
        super.popularity = model.popularity;
        super.Rating = model.vote_average;

        /*for(ModelGenres genres : model.genres){
            super.addGeneros(genres.name);
        }*/

        super.Tipo = General.MOVIE_TYPE;
        super.source = General.NET_SOURCE;
        super.job = model.job;
    }
}
