package com.wyrnlab.jotdownthatmovie.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AudiovisualInterface implements Serializable {
    protected int Id;
    protected Double Rating;
    protected String Titulo;
    protected String TituloOriginal;
    protected String Image_path;
    protected byte[] image;
    protected String Anyo;
    protected String Descripcion;
    protected List<String> Generos;
    protected List<String> Directores;
    protected String Tipo;
    protected String Seasons;
    protected String source;
    protected String originalLanguage;
    protected List<AudiovisualInterface> similars;
    protected List<AudiovisualInterface> crew = new ArrayList<AudiovisualInterface>();
    protected List<AudiovisualInterface> cast = new ArrayList<AudiovisualInterface>();
    protected Boolean viewed;
    protected String knownFor;
    protected String character;
    protected Double popularity;

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    protected String job;

    public String getKnownFor() {
        return knownFor;
    }

    public void setKnownFor(String knownFor) {
        this.knownFor = knownFor;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    protected String birthday;

    public List<AudiovisualInterface> getSimilars() {
        return similars;
    }

    public void setSimilars(List<AudiovisualInterface> similars) {
        this.similars = similars;
    }

    public List<AudiovisualInterface> getCrew() {
        return crew;
    }

    public void setCrew(List<AudiovisualInterface> crew) {
        this.crew = crew;
    }

    public void addCrew(List<AudiovisualInterface> crew) {
        this.crew.addAll(crew);
    }

    public List<AudiovisualInterface> getCast() {
        return cast;
    }

    public void setCast(List<AudiovisualInterface> cast) {
        this.cast = cast;
    }

    public void addCast(List<AudiovisualInterface> cast) {
        this.cast.addAll(cast);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? "" : source;
    }

    public void setGeneros(List<String> generos) {
        Generos = generos == null ? new ArrayList<String>() : generos;
    }

    public String getSeasons() {
        return Seasons;
    }

    public void setSeasons(String seasons) {
        Seasons = seasons == null ? "" : seasons;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo == null ? "" : tipo;
    }

    public AudiovisualInterface(){
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
        this.Titulo = nombre == null ? "" : nombre;
    }

    public void setTituloOriginal(String nombre){
        this.TituloOriginal = nombre == null ? "" : nombre;
    }

    public void setImagePath(String imgp){
        this.Image_path = imgp == null ? "" : imgp;
    }

    public void setAnyo(String an){
        this.Anyo = an == null ? "" : an;
    }

    public void setDescripcion(String desc){
        this.Descripcion = desc == null ? "" : desc;
    }

    public void addGeneros(String genero){
        this.Generos.add(genero);
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

    public String getGenerosToStrig(){
        return separateByCommas(this.getGeneros());
    }

    public String getDirectoresToString(){
        return separateByCommas(this.getDirectores());
    }

    public List<String> getDirectores(){
        return this.Directores;
    }

    public byte[] getImage() { return image; }

    public void setImage(byte[] image) { this.image = image;    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public Boolean getViewed() {
        return viewed == null ? false : viewed; // TODO remove null comparasion when all devices has v2.0+
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public void setViewed(Integer viewed) {
        if(viewed == 0){
            this.viewed = false;
        } else {
            this.viewed = true;
        }
    }

    private String separateByCommas(List<String> items){
        String returnString = "";
        for(String item : items){
            returnString += item + ", ";
        }

        if(returnString.length() > 0){
            returnString = returnString.substring(0, returnString.length() - 2);
        }

        return returnString;
    }
}
