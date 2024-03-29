package com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.ModelGenres;

public class ModelPerson extends ModelMultiSearch {

    public String profile_path;
    public Integer id;
    public ModelMovie[] known_for = new ModelMovie[]{};
    public String name;
    public Double popularity;
    public String birthday;
    public String deathday;
    public ModelCast[] cast = new ModelCast[]{};
    public ModelCrew[] crew = new ModelCrew[]{};
    public String known_for_department;

    public ModelPerson(){
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
