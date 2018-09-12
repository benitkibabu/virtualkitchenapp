package com.cloudappdev.ben.virtualkitchen.main;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.base.BaseAppCompatDialog;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.presenter.LoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.cloudappdev.ben.virtualkitchen.interfaces.LoginView;


public class Login extends BaseActivity implements LoginView {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    Button signinBtn, registerBtn;
    EditText emailField, passwordField;
    LoginPresenter loginPresenter;

    AppPreference pref;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        pref = new AppPreference(this);

        if(pref.getStringVal(AppPreference.OLD_VERSION_KEY)!= null &&
                !pref.getStringVal(AppPreference.OLD_VERSION_KEY).equals(getVersion())){
            //Show new updates features
            pref.setString(AppPreference.OLD_VERSION_KEY, getVersion());
        }

        emailField =  findViewById(R.id.emailField);
        passwordField =  findViewById(R.id.password);
        signinBtn =  findViewById(R.id.signinBtn);
        registerBtn = findViewById(R.id.registerBtn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInOnClick();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, RegisterActivity.class);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(Login.this).toBundle());
                finish();
            }
        });

        Button resetPassword = findViewById(R.id.resetpasswordBtn);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPasswordResetDialog.newInstance().show(getSupportFragmentManager(), "Password Reset");
            }
        });

        loginPresenter = new LoginPresenter(this, fAuth);
    }

    void signInOnClick(){
        loginPresenter.loginClicked();
        if(validateField(emailField) && validateField(passwordField)){
            String email =  emailField.getText().toString();
            String pass =  passwordField.getText().toString();
            if(validateEmail(email)) {
                signIn(email, pass);
            }else{
                showToast("Not a valid email");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.reload();
            if(isUserVerified(user)) {
                launchMainActivity();
            }else{
                showToast(getString(R.string.verify_email));
                user.sendEmailVerification();
            }
        }
    }

    public void signIn(String email, String password){
        showProgressDialog("Signing in....");
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();
                if(task.isSuccessful()){
                    FirebaseUser user = fAuth.getCurrentUser();
                    if(user != null) {
                        if (isUserVerified(user)) {
                            launchMainActivity();
                        } else {
                            showToast(getString(R.string.verify_email));
                        }
                    }
                }else{
                    Log.e(TAG, task.getException().getMessage());
                    showToast("Failed to login!..");
                }
            }
        });
    }

    private boolean isUserVerified(FirebaseUser user){
        if(user.isEmailVerified())
            return true;
        else return false;
    }

    public void launchMainActivity(){
        if(pref.getBooleanVal(AppPreference.TERM_KEY)) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }else{
            Intent i = new Intent(this, PrivacyAndTerms.class);
            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }
    }
    void launchMoreInfoPage(){
        Intent i = new Intent(this, AdditionalInfo.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation( this).toBundle());
        finish();
    }

    @Override
    public String getEmail() {
        return emailField.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordField.getText().toString();
    }

    @Override
    public void showEmailError(int resId) {
       emailField.setError(getString(resId));
    }

    @Override
    public void showPasswordError(int resId) {
        passwordField.setError(getString(resId));
    }

    @Override
    public void startMainActivity() {
        launchMainActivity();
    }

    @Override
    public boolean signInWithEmailAndPassword(String email, String password) {
        return fAuth.signInWithEmailAndPassword(email, password).isSuccessful();
    }

    public static class ShowPasswordResetDialog extends BaseAppCompatDialog {
        private FirebaseAuth auth;
        public static ShowPasswordResetDialog newInstance(){
            ShowPasswordResetDialog fragment = new ShowPasswordResetDialog();
            Bundle arg = new Bundle();
            fragment.setArguments(arg);
            return fragment;
        }

        public ShowPasswordResetDialog(){}

        private boolean validateField(EditText editText){
            if(editText.getText().toString().isEmpty()){
                editText.setError(getString(R.string.empty_field_error));
                return false;
            }
            else
                return true;
        }

        Context mContext;
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mContext = null;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            auth = FirebaseAuth.getInstance();

            View view  = View.inflate(mContext, R.layout.reset_password, null);

            final EditText emailField = view.findViewById(R.id.emailField);

            builder.setIcon(R.mipmap.chef);
            builder.setTitle(R.string.reset_password);
            builder.setCancelable(false);
            builder.setView(view);

            builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(validateField(emailField)) {
                        String email = emailField.getText().toString();
                        if(validateEmail(email)) {
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast("Password reset email was sent");
                                            }
                                        }
                                    });
                        }else{
                            showToast("Not a correct email format");
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getDialog().dismiss();
                }
            });

            return builder.create();
        }
    }
}
