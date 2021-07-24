package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelPerson;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

public class Person extends AudiovisualInterface {
    public void setDataFromJson(ModelPerson model){
        super.Titulo = model.name;
        super.Id = model.id;
        super.Image_path = model.profile_path;
        super.Tipo = General.PERSON_TYPE;
        super.source = General.NET_SOURCE;
    }
}
