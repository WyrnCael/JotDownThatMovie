package com.wyrnlab.jotdownthatmovie.Model;

import java.io.Serializable;

public class Trailer implements Serializable {
    public String id;
    public String iso_639_1;
    public String iso_3166_1;
    public String key;
    public String name;
    public String site;
    public String size;
    public String tpye;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTpye() {
        return tpye;
    }

    public void setTpye(String tpye) {
        this.tpye = tpye;
    }
}
