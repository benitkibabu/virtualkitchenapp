package com.cloudappdev.ben.virtualkitchen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Recipes implements Serializable{
    @SerializedName("publisher")
    String publisher;
    @SerializedName("f2f_url")
    String url;
    @SerializedName("title")
    String title;
    @SerializedName("source_url")
    String source;
    @SerializedName("recipe_id")
    String rid;
    @SerializedName("image_url")
    String imageUrl;
    @SerializedName("social_rank")
    double socialRank;
    @SerializedName("publisher_url")
    String publisherUrl;

    @SerializedName("ingredients")
    List<String> ingredients;


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getSocialRank() {
        return socialRank;
    }

    public void setSocialRank(double socialRank) {
        this.socialRank = socialRank;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
