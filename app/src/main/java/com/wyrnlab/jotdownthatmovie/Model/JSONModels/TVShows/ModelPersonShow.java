package com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCast;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;

public class ModelPersonShow extends ModelMultiSearch {

    public String profile_path;
    public Integer id;
    public ModelMovie[] known_for = new ModelMovie[]{};
    public String name;
    public Double popularity;
    public String birthday;
    public String deathday;
    public ModelCastShow[] cast = new ModelCastShow[]{};
    public ModelCrewShow[] crew = new ModelCrewShow[]{};
    public String known_for_department;

    public ModelPersonShow(){
        super.media_type = "person";
    }

    public Integer getId(){
        return id;
    }

    public Byte[] getImage(){
        return new Byte[]{};
    }

    public String getImagePath(){
        return profile_path;
    }

    public String getTitulo(){
        return name;
    }

    public Double getRating(){
        return popularity;
    }

    public String getSource(){
        return "";
    }
}
