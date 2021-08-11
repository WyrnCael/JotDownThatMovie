package com.wyrnlab.jotdownthatmovie.Model;

import android.content.Context;

public abstract class RowItemInterface {
    protected int id;
    protected Object imageId;
    protected String title;
    protected String desc = "";
    protected String type;
    protected Object object;
    protected Context context;
    protected String source;

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
