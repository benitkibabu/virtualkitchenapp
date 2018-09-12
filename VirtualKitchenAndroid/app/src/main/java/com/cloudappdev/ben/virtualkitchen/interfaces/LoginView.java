package com.cloudappdev.ben.virtualkitchen.interfaces;

public interface LoginView {
    String getEmail();
    String getPassword();

    void showEmailError(int resId);
    void showPasswordError(int resId);

    void startMainActivity();

    boolean signInWithEmailAndPassword(String email, String password);
}
