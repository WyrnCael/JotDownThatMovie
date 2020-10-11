package com.wyrnlab.jotdownthatmovie.Model;

import android.os.Debug;
import android.util.Log;

import org.jsoup.nodes.Element;

public class Streaming {
    public String imageUrl;
    public String url;
    public Boolean isPaid;
    public String price;

    public Streaming(){}

    public Streaming(Element li){
        this.url = li
                .select("a")
                .first()
                .attr("href");

        this.imageUrl = li
                .select("img")
                .first()
                .attr("src");

        if(li.select("span.price").first() == null){
            this.isPaid = false;
        } else {
            this.price = li
                    .select("span.price")
                    .first()
                    .text();
            this.isPaid = true;
        }
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
