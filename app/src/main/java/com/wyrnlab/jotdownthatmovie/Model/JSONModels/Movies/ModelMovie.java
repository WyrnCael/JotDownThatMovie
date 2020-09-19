package com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies;

import com.wyrnlab.jotdownthatmovie.Model.ModelGenres;

public class ModelMovie {

    public Boolean adult;
    public String backdrop_path;
    public Object belongs_to_collection;
    public Integer budget;
    public ModelGenres[] genres = new ModelGenres[]{};
    public String homepage;
    public Integer id;
    public String imdb_id;
    public String original_language;
    public String original_title;
    public String overview;
    public Double popularity;
    public String  poster_path;
    public ModelProductionCompanies[] production_companies = new ModelProductionCompanies[]{};
    public ModelProductionCountries[] production_countries = new ModelProductionCountries[]{};
    public String release_date;
    public Long revenue;
    public Integer runtime;
    public ModelSpokenLanguages[] spoken_languages = new ModelSpokenLanguages[]{};
    public String status;
    public String tagline;
    public String title;
    public Boolean video;
    public Double vote_average;
    public Integer vote_count;

    public ModelMovie(){

    }

    public class ModelProductionCompanies{
        public String name;
        public Integer id;
        public String logo_path;
        public String origin_country;
    }

    public class ModelProductionCountries{
        public String iso_3166_1;
        public String name;
    }

    public class ModelSpokenLanguages{
        public String iso_639_1;
        public String name;
    }
}
