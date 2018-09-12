package com.cloudappdev.ben.virtualkitchen.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.activities.MyIngredients;
import com.cloudappdev.ben.virtualkitchen.activities.ProfilePage;
import com.cloudappdev.ben.virtualkitchen.activities.RecipesActivity;
import com.cloudappdev.ben.virtualkitchen.activities.SettingsActivity;
import com.cloudappdev.ben.virtualkitchen.adapter.CustomMoodAdapter;
import com.cloudappdev.ben.virtualkitchen.adapter.GridSpacingItemDecoration;
import com.cloudappdev.ben.virtualkitchen.app.AppController;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.cloudappdev.ben.virtualkitchen.models.Emotes;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import com.newrelic.agent.android.NewRelic;

public class MainActivity extends BaseActivity {
    private final String TAG = this.getClass().getName();

    private TextView nameTv;
    private CircleImageView profileImage, moodBtn;

    private FirebaseUser user;
    private FirebaseAuth fAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private AppPreference pref;
    private SQLiteHandler db;

    //adds
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewRelic.withApplicationToken( "AA570ca36b1d8d1fc99d9ca834e67da8f61782f1d4" ).start(this.getApplication());

        fAuth = FirebaseAuth.getInstance();
        db = new SQLiteHandler(this);
        pref = new AppPreference(this);

        MobileAds.initialize(this,
                getString(R.string.ad_ap_id));
//
//        PackageManager pm = getPackageManager();
//        if(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS))

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_fullscreen_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                AppController.getInstance().setNavFragment("R");
                Intent i = new Intent(MainActivity.this, RecipesActivity.class);
                startActivity(i);
            }
        });

        //profileImage = findViewById(R.id.profile_img);
        nameTv = findViewById(R.id.name_tv);

        moodBtn = findViewById(R.id.mood_btn);
        moodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMoodDialog.newInstance().show(getSupportFragmentManager(), "MoodDialog");
            }
        });

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabSelector);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_recipe) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        AppController.getInstance().setNavFragment("R");
                        Intent i = new Intent(MainActivity.this, RecipesActivity.class);
                        startActivity(i);
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                } else if (id == R.id.action_favourite) {
                    AppController.getInstance().setNavFragment("F");
                    Intent i = new Intent(MainActivity.this, RecipesActivity.class);
                    startActivity(i);
                } else if (id == R.id.action_ingredient) {
                    Intent i = new Intent(MainActivity.this, MyIngredients.class);
                    startActivity(i);
                }
                return false;
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    void logoutUser() {
        fAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        LoginManager.getInstance().logOut();

        launchLoginPage();
    }

    void loadProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView recipeCount = findViewById(R.id.recipe_count);
        TextView ingredientCount = findViewById(R.id.ingredient_count);

        int rCount = db.getRecipeCount();
        int iCount = db.getIngredientCount();

        if (user != null) {
            user.reload();
            //nameTv.setText(user.getDisplayName());
            setTitle(user.getDisplayName());

            recipeCount.setText(String.valueOf(rCount));
            ingredientCount.setText(String.valueOf(iCount));
        } else {
            logoutUser();
        }
    }

    public void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            loadProfile();
        } else {
            Snackbar.make(profileImage, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
        }

    }

    private void launchLoginPage() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish();
    }

    public static class ShowMoodDialog extends AppCompatDialogFragment {
        private static final String ITEM_NAME = "name";

        public static ShowMoodDialog newInstance() {
            return new ShowMoodDialog();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            // request a window without the title
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            int spanCount = 3; // 3 columns
            int spacing = 50; // 50px
            boolean includeEdge = true;

            final View view = View.inflate(mContext, R.layout.my_mood_layout, null);
            final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));

            recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            final CustomMoodAdapter adapter = new CustomMoodAdapter(mContext,
                    R.layout.mood_item);

            recyclerView.setAdapter(adapter);

            final List<Emotes> emotes = new ArrayList<>();

            emotes.add(new Emotes("Feel Happy", 0x1F601));
            emotes.add(new Emotes("Feel Sad", 0x1F621));
            emotes.add(new Emotes("Feel Dizzy", 0x1F635));
            emotes.add(new Emotes("Feel Warm", 0x1F60E));
            emotes.add(new Emotes("Feel Cold", 0x1F630));
            emotes.add(new Emotes("Feel Tired", 0x1F62B));
            emotes.add(new Emotes("Feel Sick", 0x1F637));
            emotes.add(new Emotes("Festive Mood", 0x2744));
            emotes.add(new Emotes("Love Mood", 0x1F60D));

            adapter.addAll(emotes);



            adapter.setOnItemClickListener(new CustomMoodAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(getActivity(), RecipesActivity.class);
                    AppController.getInstance().searchKey = emotes.get(position).getLabel();
                    AppController.getInstance().setNavFragment("R");
                    startActivity(i);
                }
            });

            builder.create().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            builder.setView(view);

            return builder.create();
        }

        public ShowMoodDialog() {
        }

        @Override
        public void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        } else if (id == R.id.action_profile) {
            Intent i = new Intent(MainActivity.this, ProfilePage.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
