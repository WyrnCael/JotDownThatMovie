package com.wyrnlab.jotdownthatmovie.Model;

import android.content.Context;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCast;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCredits;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelPerson;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

public class Person extends AudiovisualInterface {
    public void setDataFromJson(ModelPerson model, Context context){
        super.Titulo = model.name;
        super.Id = model.id;
        super.Image_path = model.profile_path;
        super.Tipo = General.PERSON_TYPE;
        super.source = General.NET_SOURCE;
        super.knownFor = SetTheLanguages.getDepartamentsTranslation(context, model.known_for_department);
        super.birthday = model.birthday;
    }

    public void setCrewFromJSON(ModelCrew crew){
        super.Titulo = crew.title;
        super.Id = crew.id;
        super.Image_path = crew.poster_path;
        super.Tipo = General.PERSON_TYPE;
        super.source = General.NET_SOURCE;
    }

    public void setCastFromJSON(ModelCast cast){
        super.Titulo = cast.title;
        super.Id = cast.id;
        super.Image_path = cast.poster_path;
        super.character = cast.character;
        super.Tipo = General.PERSON_TYPE;
        super.source = General.NET_SOURCE;
    }
}
