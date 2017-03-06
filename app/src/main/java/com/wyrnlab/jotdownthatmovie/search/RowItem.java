package com.wyrnlab.jotdownthatmovie.search;

public class RowItem {
	private int id;
    private String imageId;
    private String title;
    private String desc;
     
    public RowItem(int id, String imageId, String title, String desc) {
    	this.id = id;
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
    }
    public int getId(){
    	return this.id;
    }
    public void setId(int id){
    	this.id = id;
    }
    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
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
}
