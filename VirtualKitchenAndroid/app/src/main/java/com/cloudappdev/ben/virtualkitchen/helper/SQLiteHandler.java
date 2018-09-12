package com.cloudappdev.ben.virtualkitchen.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cloudappdev.ben.virtualkitchen.models.Ingredient;
import com.cloudappdev.ben.virtualkitchen.models.MyRecipes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benit Kibabu on 26/05/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "recipe_db";

    FirebaseAuth auth;
    FirebaseUser user;

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyRecipes.CREATE_TABLE);
        db.execSQL(Ingredient.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyRecipes.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Ingredient.TABLE_NAME);
        onCreate(db);
    }

    public long addFavourite(MyRecipes mr){
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(MyRecipes.TABLE_NAME, null, mr.DB_VALUES());
        db.close();
        return id;
    }
    public long addIngredient(Ingredient ig){
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(Ingredient.TABLE_NAME, null, ig.DB_VALUES());
        db.close();
        return id;
    }
    public int deleteAllFavourite(){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(MyRecipes.TABLE_NAME, null, null);
        db.close();
        return i;
    }
    public int deleteAllIngredient(){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(Ingredient.TABLE_NAME, null, null);
        db.close();
        return i;
    }

    public int deleteFavourite(MyRecipes mr){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(Ingredient.TABLE_NAME, MyRecipes.C_id +" = ?",
                new String[]{String.valueOf(mr.getId())});
        db.close();
        return i;
    }
    public int deleteIngredient(Ingredient ig){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(Ingredient.TABLE_NAME, Ingredient.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ig.getId())});
        db.close();
        return i;
    }

    public int getIngredientCount(){
        String query = "SELECT * FROM " + Ingredient.TABLE_NAME + " WHERE "+ Ingredient.COLUMN_UID
                +  " = '" + user.getUid() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = c.getCount();
        c.close();
        return count;
    }
    public int getRecipeCount(){
        String query = "SELECT * FROM " + MyRecipes.TABLE_NAME + " WHERE "+ MyRecipes.C_uid + " = '" + user.getUid() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public Ingredient getIngredient(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Ingredient i = null;
        Cursor cursor = db.query(Ingredient.TABLE_NAME,
                new String[]{Ingredient.COLUMN_ID, Ingredient.COLUMN_TEXT, Ingredient.COLUMN_WEIGHT,
                        Ingredient.COLUMN_UID, Ingredient.COLUMN_TIMESTAMP},
                Ingredient.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            i = new Ingredient(
                    cursor.getInt(cursor.getColumnIndex(Ingredient.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_TEXT)),
                    cursor.getDouble(cursor.getColumnIndex(Ingredient.COLUMN_WEIGHT)),
                    cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_UID)),
                    cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_TIMESTAMP))
            );

            cursor.close();
        }

        db.close();
        return i;
    }

    public List<Ingredient> getMyIngredients(){
        List<Ingredient> ingredients = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Ingredient.TABLE_NAME + " WHERE "+ Ingredient.COLUMN_UID +  " = '" + user.getUid() + "'" +
                " ORDER BY " +
                Ingredient.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ingredient i = new Ingredient(
                        cursor.getInt(cursor.getColumnIndex(Ingredient.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_TEXT)),
                        cursor.getDouble(cursor.getColumnIndex(Ingredient.COLUMN_WEIGHT)),
                        cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_UID)),
                        cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_TIMESTAMP))
                );

                ingredients.add(i);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        return ingredients;
    }

    public MyRecipes getFavourite(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MyRecipes.TABLE_NAME + " ORDER BY "+
                " WHERE "+ MyRecipes.C_id
                + " = " + id + MyRecipes.COLUMN_TIMESTAMP  + " DESC";
        Cursor c = db.rawQuery(selectQuery, null);
        MyRecipes m = null;
        if(c != null) {
            c.moveToFirst();
            m = new MyRecipes();
            m.setId(c.getInt(c.getColumnIndex(MyRecipes.C_id)));
            m.setCalories(c.getDouble(c.getColumnIndex(MyRecipes.C_calories)));
            m.setCautions(c.getString(c.getColumnIndex(MyRecipes.C_cautions)));
            m.setDietLabels(c.getString(c.getColumnIndex(MyRecipes.C_dietLabels)));
            m.setHealthLabels(c.getString(c.getColumnIndex(MyRecipes.C_healthLabels)));

            m.setImage(c.getString(c.getColumnIndex(MyRecipes.C_image)));
            m.setIngredientCount(c.getInt(c.getColumnIndex(MyRecipes.C_ingredientCount)));
            m.setIngredientLines(c.getString(c.getColumnIndex(MyRecipes.C_ingredientLines)));
            m.setLabel(c.getString(c.getColumnIndex(MyRecipes.C_label)));
            m.setShareAs(c.getString(c.getColumnIndex(MyRecipes.C_shareAs)));
            m.setSource(c.getString(c.getColumnIndex(MyRecipes.C_source)));

            m.setTotalWeight(c.getDouble(c.getColumnIndex(MyRecipes.C_totalWeight)));
            m.setUri(c.getString(c.getColumnIndex(MyRecipes.C_uri)));
            m.setUrl(c.getString(c.getColumnIndex(MyRecipes.C_url)));
            m.setUid(c.getString(c.getColumnIndex(MyRecipes.C_uid)));
            m.setYield(c.getDouble(c.getColumnIndex(MyRecipes.C_yield)));

            m.setTimestamp(c.getString(c.getColumnIndex(MyRecipes.COLUMN_TIMESTAMP)));

            c.close();
        }

        return m;
    }

    public List<MyRecipes> getMyFavourite(){
        List<MyRecipes> favourites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MyRecipes.TABLE_NAME + " WHERE "+ MyRecipes.C_uid
                +  " = '" + user.getUid() + "'"+ " ORDER BY "+
                MyRecipes.COLUMN_TIMESTAMP  + " DESC";
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                MyRecipes m = new MyRecipes();
                m.setId(c.getInt(c.getColumnIndex(MyRecipes.C_id)));
                m.setCalories(c.getDouble(c.getColumnIndex(MyRecipes.C_calories)));
                m.setCautions(c.getString(c.getColumnIndex(MyRecipes.C_cautions)));
                m.setDietLabels(c.getString(c.getColumnIndex(MyRecipes.C_dietLabels)));
                m.setHealthLabels(c.getString(c.getColumnIndex(MyRecipes.C_healthLabels)));

                m.setImage(c.getString(c.getColumnIndex(MyRecipes.C_image)));
                m.setIngredientCount(c.getInt(c.getColumnIndex(MyRecipes.C_ingredientCount)));
                m.setIngredientLines(c.getString(c.getColumnIndex(MyRecipes.C_ingredientLines)));
                m.setLabel(c.getString(c.getColumnIndex(MyRecipes.C_label)));
                m.setShareAs(c.getString(c.getColumnIndex(MyRecipes.C_shareAs)));
                m.setSource(c.getString(c.getColumnIndex(MyRecipes.C_source)));

                m.setTotalWeight(c.getDouble(c.getColumnIndex(MyRecipes.C_totalWeight)));
                m.setUri(c.getString(c.getColumnIndex(MyRecipes.C_uri)));
                m.setUrl(c.getString(c.getColumnIndex(MyRecipes.C_url)));
                m.setUid(c.getString(c.getColumnIndex(MyRecipes.C_uid)));
                m.setYield(c.getDouble(c.getColumnIndex(MyRecipes.C_yield)));

                m.setTimestamp(c.getString(c.getColumnIndex(MyRecipes.COLUMN_TIMESTAMP)));

                favourites.add(m);
            }
            while(c.moveToNext());
        }
        c.close();
        db.close();
        return favourites;
    }
}
