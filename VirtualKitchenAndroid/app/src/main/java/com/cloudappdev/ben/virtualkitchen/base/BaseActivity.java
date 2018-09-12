package com.cloudappdev.ben.virtualkitchen.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.rest.APIService;
import com.cloudappdev.ben.virtualkitchen.rest.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * Created by Benit Kibabu on 19/05/2018.
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    public final String BASE_INTERNAL_API_URL = "https://virtualk.herokuapp.com/api/";
    public final String SENDER_ID = "1059451623513";
    public final String EXTRA_MESSAGE = "message";
    public final String DISPLAY_MSG_ACTION = "DISPLAY_MESSAGE";
    public final String TESCO_API = "http://itrackerapp.gear.host/ncigo/";
    public final String UPCDB = "http://api.upcdatabase.org/json/";
    //http://api.upcdatabase.org/json/APIKEY/CODE
    public final String UPCLOOKUP = "https://api.upcitemdb.com/prod/trial/";
    //http://api.upcdatabase.org/json/APIKEY/CODE
    public final String FOODFORK_RECIPE_API = "http://food2fork.com/api/";
    public final String FOODFORK_RECIPE_SEARCH = "http://food2fork.com/api/";
    public final String EDAMAM_RECIPE_API = "https://api.edamam.com/";
    public final String BREWERY_API = "";

    public String EDAMAM_APP_ID (){return getString(R.string.edamam_api_id);}
    public String EDAMAM_APP_KEY(){return getString(R.string.edamam_api_key);}
    public String getF2FKey(){
        return getString(R.string.f2f_key);
    }

    public void showSnackBar(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog(String t) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(t);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public APIService getBaseAPIService() {
        return RetrofitClient.getClient(BASE_INTERNAL_API_URL).create(APIService.class);
    }

    public APIService getAPIService(String externalAPI) {
        return RetrofitClient.getClient(externalAPI).create(APIService.class);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getVersion(){
        PackageInfo pInfo;
        String version = "Null";
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void displayMessage(String message){
        Intent i = new Intent(DISPLAY_MSG_ACTION);
        i.putExtra(EXTRA_MESSAGE, message);
        this.sendBroadcast(i);
    }

    public boolean validateField(EditText editText){
        if(editText.getText().toString().isEmpty()){
            editText.setError(getString(R.string.empty_field_error));
            return false;
        }
        else
            return true;
    }

    public boolean validateEmail(String str){
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}
