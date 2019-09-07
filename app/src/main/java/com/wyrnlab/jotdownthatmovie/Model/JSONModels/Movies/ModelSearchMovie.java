package com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies;

public class ModelSearchMovie {
    public Integer page;
    public ModelMovie[] results = new ModelMovie[]{};
    public Integer total_results;
    public Integer total_pages;

    public ModelSearchMovie(){

    }
}
