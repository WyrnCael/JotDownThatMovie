package com.wyrnlab.jotdownthatmovie.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wyrnlab.jotdownthatmovie.api.search.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.api.search.Movies.Pelicula;
import com.wyrnlab.jotdownthatmovie.api.search.TVShows.TVShow;

public class General {
	public final static String URLPRINCIPAL = "https://api.themoviedb.org/";
	public final static String APIKEY = "15ff919fb76802006d34be91f8ecdc3f";
	public final static String YAPIKEY = "AIzaSyC1yfVSgg9_qqxh1nfjA6zIACzO9kU53b8";
	public static String base_url = null;
	public static List<Pelicula> peliculasBuscadas;
	public static List<TVShow> showsBuscados;
	public static List<AudiovisualInterface> searchResults;
	
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
		Collections.sort(searchResults, new Comparator<AudiovisualInterface>(){
			public int compare(AudiovisualInterface mov1, AudiovisualInterface mov2) {
				// ## Ascending order
				return mov2.getAnyo().compareToIgnoreCase(mov1.getAnyo());
			}
		});
	}

	public static List<AudiovisualInterface> getsSarchResults(){
		return searchResults;
	}
}