package com.cloudappdev.ben.virtualkitchen.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by Benit Kibabu on 26/05/2017.
 */

public class AppPreference {

    private SharedPreferences pref;
    private Editor editor;

    public  final String isLoggedIn = "isLoggedIn";
    public static final String TERM_KEY = "terms";
    public static final String OLD_VERSION_KEY = "key_old_version";

    public AppPreference(Context context){
       pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setString(String key, String val){
        editor = pref.edit();
        editor.putString(key, val);
        editor.apply();
    }
    public void setBoolean(String key, boolean val){
        editor = pref.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public void setInteger(String key, int val){
        editor = pref.edit();
        editor.putInt(key, val);
        editor.apply();
    }


    public String getStringVal(String key){
        return pref.getString(key, "");
    }
    public boolean getBooleanVal(String key){
        return pref.getBoolean(key, false);
    }

    public int getInteger(String key){
        return pref.getInt(key, 0);
    }

}
