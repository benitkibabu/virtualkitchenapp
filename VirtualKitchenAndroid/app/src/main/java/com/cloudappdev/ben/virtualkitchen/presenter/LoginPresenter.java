package com.cloudappdev.ben.virtualkitchen.presenter;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.interfaces.LoginView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenter {
    private LoginView view;
    private FirebaseAuth fAuth;

    public LoginPresenter(LoginView view, FirebaseAuth fAuth) {
        this.view = view;
        this.fAuth = fAuth;
    }

    public void loginClicked() {
        String email = view.getEmail();
        String password = view.getPassword();
        if(email.isEmpty()){
            view.showEmailError(R.string.empty_field_error);
        }else if(password.isEmpty()){
            view.showPasswordError(R.string.empty_field_error);
        }
        boolean isSuccessful = view.signInWithEmailAndPassword(email, password);
        if(isSuccessful){
            view.startMainActivity();
        }
    }
}
