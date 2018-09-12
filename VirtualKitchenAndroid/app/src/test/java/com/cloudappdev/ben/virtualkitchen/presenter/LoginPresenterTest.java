package com.cloudappdev.ben.virtualkitchen.presenter;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.interfaces.LoginView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    private LoginView view;
    @Mock
    private FirebaseAuth service;
    private LoginPresenter presenter;

    @Before
    public void setUp() throws Exception{
        presenter = new LoginPresenter(view, service);
    }

    @Test
    public void showErrorWhenEmailEmpty() throws Exception {
        when(view.getEmail()).thenReturn("");
        presenter.loginClicked();

        verify(view).showEmailError(R.string.empty_field_error);
    }
//    @Test
//    public void showErrorWhenEmailNotValid() throws Exception {
//        when(view.getEmail()).thenReturn("virtualkitchen@gmail.com");
//        presenter.loginClicked();
//
//        verify(view).showEmailError(R.string.empty_field_error);
//    }
    @Test
    public void showErrorWhenPasswordEmpty() throws Exception {
        when(view.getEmail()).thenReturn("virtualkitchen@gmail.com");
        when(view.getPassword()).thenReturn("");
        presenter.loginClicked();

        verify(view).showPasswordError(R.string.empty_field_error);
    }

    @Test
    public void shouldLoadMainActivityOnSuccessLogin() throws Exception {
        when(view.getEmail()).thenReturn("virtualkitchen@gmail.com");
        when(view.getPassword()).thenReturn("1234fortnight");
        when(view.signInWithEmailAndPassword("virtualkitchen@gmail.com", "1234fortnight"))
                .thenReturn(true);

        presenter.loginClicked();
        verify(view).startMainActivity();
    }
}