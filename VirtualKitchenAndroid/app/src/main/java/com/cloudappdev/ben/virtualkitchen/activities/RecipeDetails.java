package com.cloudappdev.ben.virtualkitchen.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.app.AppController;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.cloudappdev.ben.virtualkitchen.models.MyRecipes;
import com.cloudappdev.ben.virtualkitchen.rest.APIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetails extends BaseActivity {

    Button instructionBtn, findBtn;
    private ProgressDialog mProgressDialog;
    TextView title ,ingredients, nutrition,summary;
    ImageView img, fav_icon;
    FloatingActionButton fab;

    List<MyRecipes> recipeList;
    MyRecipes r;

    AppPreference pref;
    SQLiteHandler db;

    FirebaseUser user;
    FirebaseAuth fAuth;

    private boolean isMyFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        db = new SQLiteHandler(this);
        pref = new AppPreference(this);

        fab =  findViewById(R.id.fab);
        title =findViewById(R.id.title);
        ingredients = findViewById(R.id.ingredient);
        nutrition =  findViewById(R.id.nutrition);
        summary =  findViewById(R.id.summary);
        instructionBtn =  findViewById(R.id.instruction_btn);
        fav_icon = findViewById(R.id.fav_icon);
        img = findViewById(R.id.recipe_img);
        findBtn = findViewById(R.id.view_ingredient);

        final String f = AppController.getInstance().getNavFragment();

        if(getIntent().hasExtra("Recipe") && db != null){
            r = (MyRecipes) getIntent().getSerializableExtra("Recipe");
            getMyFavourite(title);

            if(f.equals("R") && !isMyFavorite){
                fav_icon.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
            }else{
                fab.setVisibility(View.INVISIBLE);
                fav_icon.setVisibility(View.VISIBLE);
            }

            setTitle(r.getLabel());

            instructionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RecipeDetails.this, WebViewActivity.class);
                    i.putExtra("URL", r.getUrl());
                    i.putExtra("R", r);
                    startActivity(i);
                }
            });

            findBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Coming Soon!", Snackbar.LENGTH_LONG).show();
                }
            });

            title.setText(r.getLabel());

            String nut = "";
            String sum = "";
            int count = r.getIngredientCount();

            String i = count+ " Ingredients: \n" + r.getIngredientLines();
            nut += "Diet Labels: \n" +r.getDietLabels();
            nut += "\n\nHealth Labels: \n" +r.getHealthLabels();
            nut += "\n\nCalories: \n" + Math.floor(r.getCalories()) + "/ Serving";
            nut += "\n\nTotal Weight: " + Math.floor(r.getTotalWeight());
            nut += "\n\nCautions: \n" +r.getCautions();
            sum += "Source: " + r.getSource();

            ingredients.setText(i);
            nutrition.setText(nut);
            summary.setText(sum);

            Picasso.with(getApplicationContext())
                    .load(r.getImage())
                    .placeholder(R.drawable.progress_animation)
                    .resize(512,512).centerCrop()
                    .into(img);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    r.setUid(user.getUid());
                    saveRecipe(r, view);
                }
            });
        }else{
            goBack();
        }
    }

    private void getMyFavourite(final View v) {
        showProgressDialog("Fetching Favorites...");
        recipeList = db.getMyFavourite();
        if(recipeList != null && recipeList.size() > 0){
            for(MyRecipes rs : recipeList){
                if(rs.getLabel().equals(r.getLabel())){
                    isMyFavorite = true;
                    fab.setVisibility(View.INVISIBLE);
                    fav_icon.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        hideProgressDialog();
    }

    void saveRecipe(final MyRecipes recipe, final View view) {
        showProgressDialog("Saving!...");
        long id = db.addFavourite(recipe);
        if(id != 0){
            showToast("Recipe Added to Favorites!");
            fab.setVisibility(View.INVISIBLE);
            fav_icon.setVisibility(View.VISIBLE);
        }else{
            showToast("Failed to Add to Favorites!");
        }
        hideProgressDialog();
    }

    void removeRecipe(final MyRecipes recipe, final View view) {
        showProgressDialog("Removing!...");
        int i = db.deleteFavourite(recipe);
        if(i != 0){
            showToast("Item has been removed!");
            isMyFavorite = false;
            fab.setVisibility(View.VISIBLE);
            fav_icon.setVisibility(View.INVISIBLE);
        }else{
            showToast("Failed to Remove Item");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isNetworkAvailable()){
            Snackbar.make(title, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Connect method goes here
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!AppController.getInstance().getNavFragment().equals("R") || isMyFavorite) {
            getMenuInflater().inflate(R.menu.menu_recipe_deails, menu);
        }
        return true;
    }

    void goBack(){
        Intent upIntent = new Intent(this, RecipesActivity.class);
        if(NavUtils.shouldUpRecreateTask(this, upIntent)){
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
            finish();
        }else {
            NavUtils.navigateUpTo(this, upIntent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_remove) {
            r.setUid(user.getUid());

            removeRecipe(r, title);
            return true;
        }
        else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
