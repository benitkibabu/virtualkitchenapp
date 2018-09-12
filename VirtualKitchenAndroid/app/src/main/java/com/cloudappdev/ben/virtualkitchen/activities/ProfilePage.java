package com.cloudappdev.ben.virtualkitchen.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.adapter.MyStringRecyclerViewAdapter;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.base.BaseAppCompatDialog;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.cloudappdev.ben.virtualkitchen.main.Login;
import com.cloudappdev.ben.virtualkitchen.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends BaseActivity {

    SQLiteHandler db;
    FirebaseAuth auth;
    FirebaseUser user;

    CircleImageView profileImage;
    TextView recipeCount,ingredientCount, nameTv;

    private MyStringRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        setContentView(R.layout.activity_profile_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = new SQLiteHandler(this);

        profileImage= findViewById(R.id.profile_img);
        recipeCount = findViewById(R.id.recipe_count);
        ingredientCount = findViewById(R.id.ingredient_count);
        nameTv = findViewById(R.id.name_tv);

        FloatingActionButton delete = findViewById(R.id.deleteBtn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirmation.newInstance().show(getSupportFragmentManager(), "ConfirmDialog");
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class Confirmation extends BaseAppCompatDialog{

        public static Confirmation newInstance(){
            return new Confirmation();
        }

        private void logout(){ }

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
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage("Are you sure?!.")
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }

        Boolean wantToCloseDialog = false;
        @Override
        public void onStart() {
            super.onStart();
            AlertDialog d = (AlertDialog)getDialog();
            if(d != null)
            {
                Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showToast("Account deleted!");
                                        startActivity(new Intent(mContext, Login.class));
                                        wantToCloseDialog = true;
                                    }
                                }
                            });
                        }

                        if(wantToCloseDialog)
                            dismiss();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable()) {
            loadProfile();
        }else{
            Snackbar.make(nameTv, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Connect method goes here
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
        }

    }

    private void loadProfile(){
        if(user!= null){

            List<String> dataSet = new ArrayList<>();
            int rCount = db.getRecipeCount();
            int iCount = db.getIngredientCount();

            user.reload();
            nameTv.setText(user.getDisplayName());
            recipeCount.setText(String.valueOf(rCount));
            ingredientCount.setText(String.valueOf(iCount));

            dataSet.add(user.getEmail());
            for(UserInfo u : user.getProviderData()){
                dataSet.add("Provider: " + u.getProviderId());
            }
            dataSet.add("Email verified: " + String.valueOf(user.isEmailVerified()));

            adapter = new MyStringRecyclerViewAdapter(dataSet.toArray(new String[dataSet.size()]));
            recyclerView.setAdapter(adapter);

            if(user.getPhotoUrl() == null){
                Picasso.with(getApplicationContext())
                        .load(R.drawable.round_button)
                        .placeholder(R.drawable.progress_animation)
                        .resize(256,256).centerCrop()
                        .into(profileImage);
            }else {
                Picasso.with(getApplicationContext())
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.progress_animation)
                        .resize(256, 256).centerCrop()
                        .into(profileImage);
            }
        }else{
            logout();
        }
    }

    private void logout(){
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    void goBack(){
        Intent upIntent = new Intent(this, MainActivity.class);
        if(NavUtils.shouldUpRecreateTask(this, upIntent)){
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities(ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }else {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if(id == android.R.id.home){
            //goBack();
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
