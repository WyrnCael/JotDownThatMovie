package data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import api.search.Pelicula;

public class General {
	public final static String URLPRINCIPAL = "https://api.themoviedb.org/";
	public final static String APIKEY = "/*YOUR_TMDB_APIKEY*/";
	public final static String YAPIKEY = "/*YOUR_YOUTUBE_APIKEY*/";
	public static String base_url;
	public static List<Pelicula> peliculasBuscadas;
	
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
	
}
