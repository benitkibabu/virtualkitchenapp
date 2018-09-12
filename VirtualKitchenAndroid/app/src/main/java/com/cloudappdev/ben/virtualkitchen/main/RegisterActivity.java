package com.cloudappdev.ben.virtualkitchen.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity{
    private static final String TAG = "RegisterActivity";
    private static final int RC_SIGN_IN = 9001;

    EditText  emailField, password;
    Button registerBtn, signBtn, termsBtn;
    CheckBox termsCheckBox;
    LoginButton loginButton;

    AppPreference pref;
    SQLiteHandler db;

    private FirebaseAuth fAuth;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;

    private final String privacyPolicy = "https://docs.google.com/document/d/1Y3HXg9bmnJrWTpNonBUxphto5PCe2O5ezlISdaILfCM";
    private final String termsURL = "https://docs.google.com/document/d/1xqH7lkmlKYRb_M5m27GBc0u2zGyz7WwcifgH56u7lpw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        pref = new AppPreference(this);
        db = new SQLiteHandler(this);

        termsBtn = findViewById(R.id.termBtn);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        emailField = findViewById(R.id.emailField);
        password =  findViewById(R.id.passwordField);

        signBtn = findViewById(R.id.signinBtn);
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, Login.class);
                startActivity(i,  ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this).toBundle());
                finish();
            }
        });

        registerBtn =  findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsCheckBox.isChecked()) {
                    if (validateField(emailField) && validateField(password)) {
                        String email = emailField.getText().toString();
                        String pass = password.getText().toString();
                        register(email, pass);
                    }
                }else{
                    showSnackBar(emailField,"Please Accept the terms before continuing");
                }
            }
        });

        loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsCheckBox.isChecked()){
                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            handleFacebookLogin(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "facebook:onCancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG, "facebook:onCancel");
                        }
                    });
                }else{
                    showSnackBar(emailField,"Please Accept the terms before continuing");
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton  = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsCheckBox.isChecked()) {
                    Intent i = googleSignInClient.getSignInIntent();
                    startActivityForResult(i, RC_SIGN_IN);
                }else{
                    showSnackBar(emailField,"Please Accept the terms before continuing");
                }
            }
        });

        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsURL));
                startActivity(browserIntent);
            }
        });
    }

    private void register(String email, String password){
        showProgressDialog("Creating New User...");
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = fAuth.getCurrentUser();
                            if(user != null) {
                                launchMoreInfoPage("R");
                            }

                        }else {
                            Log.e(TAG, task.getException().getMessage());
                            showSnackBar(emailField, getString(R.string.register_failed));
                        }
                        hideProgressDialog();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable()){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                user.reload();
                if(user.isEmailVerified()) {
                    launchMainActivity();
                }else{
                    showToast(getString(R.string.verify_email));
                }
            }
        }else{
            showToast(getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    void firebaseAuthWithGoogle(GoogleSignInAccount account){
        showProgressDialog("Signing in with Google!...");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            launchMoreInfoPage("F");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showSnackBar(emailField, "Authentication Failed." );
                        }
                    }
                });
    }

    void handleFacebookLogin(AccessToken token){
        showProgressDialog("Signing in with Facebook!...");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if(task.isSuccessful()){
                            launchMoreInfoPage("F");
                        }else{
                            showToast("Failed to register with credential");
                        }
                    }
                });
    }

    void launchMoreInfoPage(String r){
        Intent i = new Intent( this, AdditionalInfo.class);
        i.putExtra("R", r);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation( this).toBundle());
        finish();
    }
    void launchLoginPage(){
        Intent i = new Intent( this, Login.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation( this).toBundle());
        finish();
    }
    public void launchMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }
}
