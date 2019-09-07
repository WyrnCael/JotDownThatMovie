package com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows;

public class ModelSearchTVShow {
    public Integer page;
    public ModelShow[] results = new ModelShow[]{};
    public Integer total_results;
    public Integer total_pages;
}
