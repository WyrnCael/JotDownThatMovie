package com.wyrnlab.jotdownthatmovie.Utils;

/**
 * Created by Jota on 06/03/2017.
 */

public class SetTheLanguages {

    public static String getLanguage(String language){
        if(language.compareTo("espa�ol") == 0){
            return "es";
        } else {
            return "en";
        }
    }
}
