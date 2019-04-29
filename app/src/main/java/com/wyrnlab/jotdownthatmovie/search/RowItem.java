package com.wyrnlab.jotdownthatmovie.search;

public class RowItem {
	private int id;
    private Object imageId;
    private String title;
    private String desc;
    private String type;
    private Object object;
     
    public RowItem(int id, Object imageId, String title, String desc, String type, Object object) {
    	this.id = id;
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.object = object;
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


}
