package com.cloudappdev.ben.virtualkitchen.models;

import android.content.ContentValues;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ben on 21/10/2016.
 */

public class Ingredient implements Serializable{
    @SerializedName("id")
    int id;
    @SerializedName("text")
    String text;
    @SerializedName("weight")
    double weight;
    @SerializedName("uid")
    String uid;
    @SerializedName("timestamp")
    String timestamp;

    public Ingredient() {
    }

    public Ingredient(int id, String text, double weight, String uid, String timestamp) {
        this.id = id;
        this.text = text;
        this.weight = weight;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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

    public String toJSon() {
        return new GsonBuilder().create().toJson(this, Ingredient.class);
    }

    public static final String TABLE_NAME = "ingredient";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TEXT + " TEXT,"
            + COLUMN_WEIGHT + " TEXT,"
            + COLUMN_UID + " TEXT,"
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public final ContentValues DB_VALUES(){
        ContentValues v = new ContentValues();
        v.put(COLUMN_TEXT, text);
        v.put(COLUMN_WEIGHT, weight);
        v.put(COLUMN_UID, uid);
        return v;
    }
}
