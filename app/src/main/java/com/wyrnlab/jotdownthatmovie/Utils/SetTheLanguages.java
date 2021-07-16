package com.wyrnlab.jotdownthatmovie.Utils;

/**
 * Created by Jota on 06/03/2017.
 */

public class SetTheLanguages {

    public static String getLanguage(String language, String country){
        if(language.compareTo("español") == 0){
            if(country.equalsIgnoreCase("MX")){
                return "es" + "-" + country;
            } else {
                return "es" + "-" + "ES";
            }
        } else {
            return "en";
        }
    }
}
