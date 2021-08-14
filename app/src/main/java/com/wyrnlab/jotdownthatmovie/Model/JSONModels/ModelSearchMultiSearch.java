package com.wyrnlab.jotdownthatmovie.Model.JSONModels;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.MultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;

public class ModelSearchMultiSearch {
    public Integer page;
    public ModelMultiSearch[] results = new ModelMultiSearch[]{};
    public Integer total_results;
    public Integer total_pages;

    public ModelSearchMultiSearch(){

    }
}
