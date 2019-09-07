package com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies;

public class ModelCredits {
    public Integer id;
    public ModelCast[] cast = new ModelCast[]{};
    public ModelCrew[] crew = new ModelCrew[]{};

    public ModelCredits(){

    }

    public class ModelCast{
        public Integer cast_id;
        public String character;
        public String credit_id;
        public Integer gender;
        public Integer id;
        public String name;
        public Integer order;
        public String profile_path;
    }

    public class ModelCrew{
        public String credit_id;
        public String department;
        public Integer gender;
        public Integer id;
        public String job;
        public String name;
        public String profile_path;
    }
}
