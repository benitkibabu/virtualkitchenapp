package com.cloudappdev.ben.virtualkitchen.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.cloudappdev.ben.virtualkitchen.rest.APIService;
import com.cloudappdev.ben.virtualkitchen.rest.RetrofitClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ben on 17/10/2016.
 */

public class AppConfig {

    public static final String TEST_INTERNAL_API_URL = "http://127.0.0.1:3000/api/";
    public static final String BASE_INTERNAL_API_URL = "https://virtualk.herokuapp.com/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_INTERNAL_API_URL).create(APIService.class);
    }

    public static APIService getAPIService(String externalAPI) {
        return RetrofitClient.getClient(externalAPI).create(APIService.class);
    }


    public static final String SENDER_ID = "1059451623513";
    public static final String EXTRA_MESSAGE = "message";
    public static final String DISPLAY_MSG_ACTION = "DISPLAY_MESSAGE";

    public static final String TESCO_API = "http://itrackerapp.gear.host/ncigo/";
    public static final String UPCDB = "http://api.upcdatabase.org/json/";
    //http://api.upcdatabase.org/json/APIKEY/CODE
    public static final String UPLOOKUP = "https://api.upcitemdb.com/prod/trial/";
    //http://api.upcdatabase.org/json/APIKEY/CODE
    public static final String RECIPE_API = "https://api.edamam.com/";
    public static final String BREWERY_API = "";

}
