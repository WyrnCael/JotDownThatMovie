package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Model.JSONModels.WatchProviders.ModelProvider;

public class Streaming {
    public String imageUrl;
    public String url;
    public Boolean isPaid;
    public String price;

    public Streaming(){}

    public Streaming(ModelProvider provider, String link, String categoryLabel, boolean isPaid){
        this.imageUrl = General.base_url + "w92" + provider.logo_path;
        this.url = link;
        this.price = categoryLabel;
        this.isPaid = isPaid;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
