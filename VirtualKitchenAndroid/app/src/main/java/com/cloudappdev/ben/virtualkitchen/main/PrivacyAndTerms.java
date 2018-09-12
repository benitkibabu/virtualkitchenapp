package com.cloudappdev.ben.virtualkitchen.main;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.activities.WebViewActivity;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;

public class PrivacyAndTerms extends BaseActivity {

    private final String privacyPolicy = "https://docs.google.com/document/d/1Y3HXg9bmnJrWTpNonBUxphto5PCe2O5ezlISdaILfCM";
    private final String termsURL = "https://docs.google.com/document/d/1xqH7lkmlKYRb_M5m27GBc0u2zGyz7WwcifgH56u7lpw";

    AppPreference pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_and_terms);

        pref = new AppPreference(this);

        if(pref.getBooleanVal(AppPreference.TERM_KEY)){
            launchMainActivity();
        }

        final Button termsBtn = findViewById(R.id.termBtn);
        final CheckBox termsCheckBox = findViewById(R.id.termsCheckBox);

        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsURL));
                startActivity(browserIntent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.confirmBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsCheckBox.isChecked()) {
                    pref.setBoolean("terms", termsCheckBox.isChecked());
                    launchMainActivity();
                }else{
                    showSnackBar(termsBtn,"Please agree to the terms before using this app!");
                }
            }
        });
    }

    private void launchMainActivity(){
        startActivity(new Intent(PrivacyAndTerms.this, MainActivity.class));
        finish();
    }
}
