package com.wyrnlab.jotdownthatmovie.Model.JSONModels;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelPerson;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelSearchTVShow;

import org.jsoup.select.Evaluator;

public abstract class ModelMultiSearch {

    public String media_type;

    public ModelMultiSearch(){

    }

    public abstract Integer getId();
    public abstract Byte[] getImage();
    public abstract String getImagePath();
    public abstract String getTitulo();
    public abstract Double getRating();
    public abstract String getSource();

    public String getTipo(){
        return this.media_type;
    }
}
