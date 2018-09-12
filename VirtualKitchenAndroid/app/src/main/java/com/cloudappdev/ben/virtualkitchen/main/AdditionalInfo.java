package com.cloudappdev.ben.virtualkitchen.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AdditionalInfo extends BaseActivity {
    EditText displayNameField, password, confirmPassword;

    AppPreference pref;
    SQLiteHandler db;

    FirebaseAuth fAuth;
    boolean manualRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        setContentView(R.layout.activity_additional_info);

        fAuth = FirebaseAuth.getInstance();
        pref = new AppPreference(this);
        db = new SQLiteHandler(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayNameField =  findViewById(R.id.display_name);
        password =  findViewById(R.id.passwordField);
        confirmPassword =  findViewById(R.id.confirm_passwordField);

        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){
            displayNameField.setText(user.getDisplayName());
        }
        String r = getIntent().getStringExtra("R");

        if(r != null && r.equals("R")){
            password.setVisibility(View.INVISIBLE);
            confirmPassword.setVisibility(View.INVISIBLE);
            manualRegister = true;
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(manualRegister) {
                    if(validateField(displayNameField)) {
                        String name = displayNameField.getText().toString();
                        updateName(fAuth.getCurrentUser(), name);
                    }
                }else {
                    if (validateField(displayNameField) && validateField(password) && validateField(confirmPassword)) {
                        if (TextUtils.equals(password.getText().toString(), confirmPassword.getText().toString())) {
                            String name = displayNameField.getText().toString();
                            String pass = password.getText().toString();

                            updateProfile(fAuth.getCurrentUser(), name, pass);
                        } else {
                            showSnackBar(displayNameField, "Password doesn't match!..");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }
    }

    //Post Request
    private void updateProfile(final FirebaseUser user, String displayName, final String password){
        showProgressDialog("Updating Profile!...");
        UserProfileChangeRequest PCR = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(PCR)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            updatePassword(user, password);
                        }else{
                            showSnackBar(displayNameField,"Failed to update profile");
                        }

                    }
                });
    }
    private void updateName(final FirebaseUser user, String displayName){
        showProgressDialog("Updating Profile!...");
        UserProfileChangeRequest PCR = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(PCR)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sendEmailVerification();
                        }else{
                            showSnackBar(displayNameField,"Failed to update profile");
                        }

                    }
                });
    }
    private void updatePassword(FirebaseUser user, String password){
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            sendEmailVerification();
                        }else{
                            showSnackBar(displayNameField,"Failed to update password");
                        }
                    }
                });
    }
    private void sendEmailVerification() {
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fAuth.signOut();
                    showToast(getString(R.string.verify_email));
                    launchLoginActivity();
                }
            });
        }
    }

    private void launchMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

    private void launchLoginActivity(){
        Intent i = new Intent(this, Login.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }
}
