package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSOMModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.io.Serializable;

public class TVShow  extends AudiovisualInterface  implements Serializable {

    public void setDataFromJson(ModelShow model){
        super.TituloOriginal = model.original_name;
        super.Titulo = model.name;
        super.Id = model.id;
        super.Anyo = MyUtils.getYearFromDate(model.first_air_date);
        super.Image_path = model.poster_path;
        super.Rating = model.vote_average;
        super.Descripcion = model.overview == null ? "" : model.overview;

        for(ModelGenres genres : model.genres){
            super.addGeneros(genres.name);
        }

        super.Seasons = String.valueOf(model.number_of_seasons);
        super.Tipo = General.TVSHOW_TYPE;
        super.source = General.NET_SOURCE;
    }

}
