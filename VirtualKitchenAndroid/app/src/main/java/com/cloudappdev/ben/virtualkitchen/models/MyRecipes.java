package com.cloudappdev.ben.virtualkitchen.models;

import android.content.ContentValues;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Benit Kibabu on 25/05/2017.
 */

public class MyRecipes implements Serializable {
    @SerializedName("id")
    int id;
    @SerializedName("uri")
    String uri;
    @SerializedName("label")
    String label;
    @SerializedName("image")
    String image;
    @SerializedName("source")
    String source;
    @SerializedName("url")
    String url;
    @SerializedName("shareAs")
    String shareAs;
    @SerializedName("yield")
    double yield;
    @SerializedName("dietLabels")
    String dietLabels;
    @SerializedName("healthLabels")
    String healthLabels;
    @SerializedName("cautions")
    String cautions;
    @SerializedName("ingredientLines")
    String ingredientLines;
    @SerializedName("calories")
    double calories;
    @SerializedName("totalWeight")
    double totalWeight;
    @SerializedName("app_user_id")
    String uid;
    @SerializedName("ingredientCount")
    int ingredientCount;
    @SerializedName("timestamp")
    String timestamp;

    public MyRecipes() {
    }

    public MyRecipes(int id, String uri, String label, String image, String source, String url, String shareAs, double yield, String dietLabels, String healthLabels, String cautions, String ingredientLines, double calories, double totalWeight, String uid, int ingredientCount, String timestamp) {
        this.id = id;
        this.uri = uri;
        this.label = label;
        this.image = image;
        this.source = source;
        this.url = url;
        this.shareAs = shareAs;
        this.yield = yield;
        this.dietLabels = dietLabels;
        this.healthLabels = healthLabels;
        this.cautions = cautions;
        this.ingredientLines = ingredientLines;
        this.calories = calories;
        this.totalWeight = totalWeight;
        this.uid = uid;
        this.ingredientCount = ingredientCount;
        this.timestamp = timestamp;
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public void setIngredientCount(int ingredientCount) {
        this.ingredientCount = ingredientCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareAs() {
        return shareAs;
    }

    public void setShareAs(String shareAs) {
        this.shareAs = shareAs;
    }

    public double getYield() {
        return yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public String getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(String dietLabels) {
        this.dietLabels = dietLabels;
    }

    public String getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(String healthLabels) {
        this.healthLabels = healthLabels;
    }

    public String getCautions() {
        return cautions;
    }

    public void setCautions(String cautions) {
        this.cautions = cautions;
    }

    public String getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(String ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toJson() {
        return new GsonBuilder().create().toJson(this, MyRecipes.class);
    }

    public static final String TABLE_NAME = "favourites";

    public static final String C_id = "id";
    public static final String C_uri = "uri";
    public static final String C_label = "label";
    public static final String C_image = "image";
    public static final String C_source = "source";
    public static final String C_url = "url";
    public static final String C_shareAs = "shareas";
    public static final String C_yield = "yield";
    public static final String C_dietLabels = "dietlabels";
    public static final String C_healthLabels = "healthlabel";
    public static final String C_cautions = "cautions";
    public static final String C_ingredientLines = "ingredientlines";
    public static final String C_calories = "calories";
    public static final String C_totalWeight = "totalweight";
    public static final String C_uid = "uid";
    public static final String C_ingredientCount = "ingredientcount";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + C_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + C_uri + " TEXT,"
                    + C_label + " TEXT,"
                    + C_image + " TEXT,"
                    + C_source + " TEXT,"
                    + C_url + " TEXT,"
                    + C_shareAs + " TEXT,"
                    + C_yield + " TEXT,"
                    + C_dietLabels + " TEXT,"
                    + C_healthLabels + " TEXT,"
                    + C_cautions + " TEXT,"
                    + C_ingredientLines + " TEXT,"
                    + C_calories + " TEXT,"
                    + C_totalWeight + " TEXT,"
                    + C_uid + " TEXT,"
                    + C_ingredientCount + " INTEGER,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public final ContentValues DB_VALUES(){
        ContentValues v = new ContentValues();
        v.put(C_uri, uri);
        v.put(C_label, label);
        v.put(C_image, image);
        v.put(C_source, source);
        v.put(C_url, url);
        v.put(C_shareAs, shareAs);
        v.put(C_yield, yield);
        v.put(C_dietLabels, dietLabels);
        v.put(C_healthLabels, healthLabels);
        v.put(C_cautions, cautions);
        v.put(C_ingredientLines, ingredientLines);
        v.put(C_calories, calories);
        v.put(C_totalWeight, totalWeight);
        v.put(C_uid, uid);
        v.put(C_ingredientCount, ingredientCount);
        return v;
    }
}
