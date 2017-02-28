package api.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Pelicula implements Serializable{

	private int Id;
	private double Rating;
	private String Titulo;
	private String TituloOriginal;
	private String Image_path;
	private String Anyo;
	private String Descripcion;
	private List<String> Generos;
	private List<String> Directores;
	
	public Pelicula(){
		this.Generos = new ArrayList<String>();
		this.Directores = new ArrayList<String>();
	}
	
	public void setId(int id){
		this.Id = id;
	}
	
	public void setRating(double r){
		this.Rating = r;
	}
	
	public void setTitulo(String nombre){
		this.Titulo = nombre;
	}
	
	public void setTituloOriginal(String nombre){
		this.TituloOriginal = nombre;
	}
	
	public void setImagePath(String imgp){
		this.Image_path = imgp;
	}
	
	public void setAnyo(String an){
		this.Anyo = an;
	}
	
	public void setDescripcion(String desc){
		this.Descripcion = desc;
	}
	
	public void setGeneros(List<String> gen){
		this.Generos = gen;
	}
	
	public void addGeneros(String genero){
		this.Generos.add(genero);
	}
	
	public void setDirectores(List<String> dire){
		this.Directores = dire;
	}
	
	public void addDirectores(String dire){
		this.Directores.add(dire);
	}
	
	public int getId(){
		return this.Id;
	}
	
	public double getRating(){
		return this.Rating;
	}
	
	public String getTitulo(){
		return this.Titulo;
	}
	
	public String getTituloOriginal(){
		return this.TituloOriginal;
	}
	
	public String getImagePath(){
		return this.Image_path;
	}
	
	public String getAnyo(){
		return this.Anyo;
	}
	
	public String getDescripcion(){
		return this.Descripcion;
	}
	
	public List<String> getGeneros(){
		return this.Generos;
	}
	
	public List<String> getDirectores(){
		return this.Directores;
	}
}
