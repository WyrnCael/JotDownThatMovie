package com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows;

import com.wyrnlab.jotdownthatmovie.Model.ModelGenres;

public class ModelShow {
    public String backdrop_path;
    public ModelCreatedBy[] created_by = new ModelCreatedBy[]{};
    public Integer[] episode_run_time;
    public String first_air_date;
    public ModelGenres[] genres = new ModelGenres[]{};
    public String homepage;
    public Integer id;
    public Boolean in_production;
    public String[] languages;
    public String last_air_date;
    public ModelLastEpisodeAir last_episode_to_air;
    public String name;
    public Object next_episode_to_air;
    public ModelNetworks[] networks = new ModelNetworks[]{};
    public Integer number_of_episodes;
    public Integer number_of_seasons;
    public String[] origin_country;
    public String original_language;
    public String original_name;
    public String overview;
    public Double popularity;
    public String poster_path;
    public ModelProductionCompaniesTV[] production_companies = new ModelProductionCompaniesTV[]{};
    public ModelSeasons[] seasons = new ModelSeasons[]{};
    public String status;
    public String type;
    public Double vote_average;
    public Integer vote_count;

    public class ModelCreatedBy{
        public Integer id;
        public String credit_id;
        public String name;
        public Integer gender;
        public String profile_path;
    }

    public class ModelLastEpisodeAir{
        public String air_date;
        public Integer episode_number;
        public Integer id;
        public String name;
        public String overview;
        public String production_code;
        public Integer season_number;
        public Integer show_id;
        public String still_path;
        public Double vote_average;
        public Integer vote_count;
    }

    public class ModelNetworks{
        public String name;
        public Integer id;
        public String logo_path;
        public String origin_country;
    }

    public class ModelProductionCompaniesTV{
        public Integer id;
        public String logo_path;
        public String name;
        public String origin_country;
    }

    public class ModelSeasons{
        public String air_date;
        public Integer episode_count;
        public Integer id;
        public String name;
        public String overview;
        public String poster_path;
        public Integer season_number;
    }
}
