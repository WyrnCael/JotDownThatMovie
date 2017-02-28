package data;

import java.util.List;

import api.search.Pelicula;

public class General {
	public final static String URLPRINCIPAL = "https://api.themoviedb.org/";
	public final static String APIKEY = "15ff919fb76802006d34be91f8ecdc3f";
	public static String base_url;
	public static List<Pelicula> peliculasBuscadas;
	
	public static void setPeliculasBuscadas(List<Pelicula> peliculas){
		peliculasBuscadas = peliculas;
	}
	
	public static List<Pelicula> getPeliculasBuscadas(){
		return peliculasBuscadas;
	}
	
}
