package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
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
        super.knownFor = model.known_for_department;

    }

    public void setCrewFromJSON(ModelCrew crew){
        super.Titulo = crew.title;
        super.Id = crew.id;
        super.Image_path = crew.poster_path;
        super.Tipo = General.PERSON_TYPE;
        super.source = General.NET_SOURCE;
    }
}
