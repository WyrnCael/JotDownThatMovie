package com.wyrnlab.jotdownthatmovie.search;

import android.content.Context;
import android.provider.MediaStore;

import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.api.search.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.data.General;

public class RowItem {
	private int id;
    private Object imageId;
    private String title;
    private String desc;
    private String type;
    private Object object;
    private Context context;
    private String source;
     
    public RowItem(Context context, Object object) {

        AudiovisualInterface movie = (AudiovisualInterface) object;
    	this.id = movie.getId();
        this.imageId = movie.getImage() != null ? movie.getImage() : General.base_url + "w92" +  movie.getImagePath();
        this.title = movie.getTitulo();
        this.desc = (context.getResources().getString(R.string.anyo) + " " + movie.getAnyo() + " " + context.getResources().getString(R.string.valoracion) + " ");
        this.desc += movie.getRating() == 0.0 ? context.getResources().getString(R.string.notavailable) : movie.getRating();
        this.type = movie.getTipo();
        this.source = movie.getSource();
        this.object = object;
        this.context = context;
    }
    public int getId(){
    	return this.id;
    }
    public void setId(int id){
    	this.id = id;
    }
    public Object getImageId() {
        return imageId;
    }
    public void setImageId(Object imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
    public String getType() { return type; }
    public void setType(Object icon) { this.type = type; }
    public Object getObject() { return object; }
    public void setObject(Object object) { this.object = object; }
    public void setType(String type) {  this.type = type; }
    public Context getContext() { return context; }
    public void setContext(Context context) { this.context = context; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }


}
