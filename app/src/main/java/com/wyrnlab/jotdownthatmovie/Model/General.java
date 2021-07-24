package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelSearchMultiSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class General {
    public final static String URLPRINCIPAL = "https://api.themoviedb.org/";
    public final static String APIKEY = "YOUR_TMDB_APIKEY";
    public final static String YAPIKEY = "YOUR_YOUTUBE_APIKEY";
    public final static String ALL_TYPE = "All";
    public final static String MOVIE_TYPE = "Movie";
    public final static String TVSHOW_TYPE = "Show";
    public final static String PERSON_TYPE = "Person";
    public final static String VIEWED = "Viewed";
    public final static String DB_SOURCE = "DB";
    public final static String NET_SOURCE = "NET";
    public final static String SHARE_SOURCE = "SHARE";
    public final static int REQUEST_CODE_PELIBUSCADA = 5;
    public final static int RESULT_CODE_ADD = 10001;
    public final static int RESULT_CODE_REMOVED = 10002;
    public final static int RESULT_CODE_NEEDS_REFRESH = 10003;
    public final static int RESULT_CODE_SIMILAR_CLOSED = 10004;
    public static String base_url = null;
    private static List<String> MovieIdsInDB = new ArrayList<>();
    private static List<Pelicula> peliculasBuscadas;
    private static List<TVShow> showsBuscados;
    public static List<AudiovisualInterface> searchResults;
    public static Languages languageTranslations;
    public static String AppLanguage = null;
    public static String SearchLanguage = null;
    public static final String LANGUAGE_SETTINGS = "LanguageSettings";
    public static final String APP_LANGUAGE_SETTINGS = "AppLanguage";
    public static final String SEARCH_LANGUAGE_SETTINGS = "SearchLanguage";
    public static List<String> appLanguagesArray = Arrays.asList(
            "es",
            "en"
    );

    public static List<String> searchLanguagesArray = Arrays.asList(
            "en-US",
            "es-ES",
            "es-MX"
    );

    public static void setPeliculasBuscadas(List<Pelicula> peliculas){
        peliculasBuscadas = peliculas;
        Collections.sort(peliculasBuscadas, new Comparator<Pelicula>(){
            public int compare(Pelicula mov1, Pelicula mov2) {
                // ## Ascending order
                return mov2.getAnyo().compareToIgnoreCase(mov1.getAnyo());
            }
        });
    }

    public static List<Pelicula> getPeliculasBuscadas(){
        return peliculasBuscadas;
    }

    public static void setshowsBuscados(List<TVShow> shows){
        showsBuscados = shows;
        Collections.sort(showsBuscados, new Comparator<TVShow>(){
            public int compare(TVShow mov1, TVShow mov2) {
                // ## Ascending order
                return mov2.getAnyo().compareToIgnoreCase(mov1.getAnyo());
            }
        });
    }

    public static List<TVShow> getShowsBuscados(){
        return showsBuscados;
    }

    public static void setSearchResults(List<AudiovisualInterface> results){
        searchResults = results;
        /*Collections.sort(searchResults.results, new Comparator<AudiovisualInterface>(){
            public int compare(AudiovisualInterface mov1, AudiovisualInterface mov2) {
                // ## Ascending order
                return mov2.getAnyo().compareToIgnoreCase(mov1.getAnyo());
            }
        });*/
    }

    public static List<AudiovisualInterface> getsSarchResults(){
        return searchResults;
    }

    public static String getLanguageTranslations(String isoCode){
        languageTranslations = Languages.getSingleton();
        return languageTranslations.getLanguageName(isoCode);
    }
}
