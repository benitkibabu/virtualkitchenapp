package com.cloudappdev.ben.virtualkitchen.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.adapter.CustomRecycleViewAdapter;
import com.cloudappdev.ben.virtualkitchen.app.AppConfig;
import com.cloudappdev.ben.virtualkitchen.app.AppController;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.cloudappdev.ben.virtualkitchen.main.MainActivity;
import com.cloudappdev.ben.virtualkitchen.models.FFResponse;
import com.cloudappdev.ben.virtualkitchen.models.Hit;
import com.cloudappdev.ben.virtualkitchen.models.MyRecipes;
import com.cloudappdev.ben.virtualkitchen.models.Recipe;
import com.cloudappdev.ben.virtualkitchen.models.RecipeResponse;
import com.cloudappdev.ben.virtualkitchen.models.Recipes;
import com.cloudappdev.ben.virtualkitchen.rest.APIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesActivity extends BaseActivity {

    FirebaseUser user;
    FirebaseAuth auth;

    private RecyclerView recyclerView;
    private CustomRecycleViewAdapter adapter;

    List<MyRecipes> recipeList;
    int itemPosition;

    APIService service;
    AppPreference pref;
    SQLiteHandler db;
    LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = new SQLiteHandler(this);
        pref = new AppPreference(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loading = findViewById(R.id.progressBar2);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        registerForContextMenu(recyclerView);

        adapter = new CustomRecycleViewAdapter(this, R.layout.recipe_item);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CustomRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!recipeList.isEmpty()) {
                    Intent i = new Intent(RecipesActivity.this, RecipeDetails.class);
                    MyRecipes r = recipeList.get(position);
                    i.putExtra("Recipe", r);
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(RecipesActivity.this).toBundle());
                }else{
                    Snackbar.make(view, "Please wait until Items are loaded",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        adapter.setOnItemLongClickListener(new CustomRecycleViewAdapter.OnItemLongCLickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                itemPosition = position;
                Snackbar.make(view, "Would you like to remove this? " + itemPosition,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setAction("No", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
            }
        });

    }

    private void showProgressBar(){
        loading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        loading.setVisibility(View.GONE);
    }

    //Search Recipes online from external API
    public void getRecipe(final String query) {
        setTitle("Food2Fork");
        loading.setVisibility(View.VISIBLE);
       // showProgressDialog("Fetching Recipes!...");
        recipeList = new ArrayList<>();
        service =  getAPIService(FOODFORK_RECIPE_SEARCH);
        service.getF2FRecipes(getF2FKey(), query)
                .enqueue(new Callback<FFResponse>() {
                    @Override
                    public void onResponse(Call<FFResponse> call, Response<FFResponse> response) {

                        if (response.isSuccessful()) {
                            Log.i("R",response.toString() );
                            assert response.body() != null;
                            List<Recipes> recipes = response.body().getRecipes();
                            for (Recipes r : recipes) {
                                if (r != null) {
                                    MyRecipes re = new MyRecipes();
                                    re.setIngredientCount(r.getIngredients() == null ? 0 : r.getIngredients().size());
                                    re.setCalories(0);
                                    re.setImage(r.getImageUrl());
                                    re.setLabel(r.getTitle());
                                    re.setShareAs(r.getPublisherUrl());
                                    re.setSource(r.getPublisher());
                                    re.setTotalWeight(0);
                                    re.setUri(r.getUrl());
                                    re.setUrl(r.getSource());
                                    re.setYield(0);
                                    re.setCautions("");
                                    re.setHealthLabels("");
                                    re.setDietLabels("");
                                    re.setIngredientLines(r.getIngredients() == null ? "" : r.getIngredients().toString());

                                    recipeList.add(re);
                                }
                            }
                            adapter.addAll(recipeList);
                        }
                        else{
                            showSnackBar(recyclerView,"Failed to retrieve Recipes");
                            getRecipeFromEdamam(query);
                        }
                        loading.setVisibility(View.GONE);
                        //hideProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<FFResponse> call, Throwable t) {
                        loading.setVisibility(View.GONE);
                        //hideProgressDialog();
                        showSnackBar( recyclerView,"There was an error connecting to API");
                        getRecipeFromEdamam(query);
                    }
                });

    }

    private void getRecipeFromEdamam(final String query){
        setTitle("Edamam");
        loading.setVisibility(View.VISIBLE);
       // showProgressDialog("Fetching Recipes!...");
        recipeList = new ArrayList<>();
        service = AppConfig.getAPIService(EDAMAM_RECIPE_API);
        service.fetchOnlineRecepes(query, EDAMAM_APP_ID(), EDAMAM_APP_KEY())
        .enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {

                if(response.isSuccessful()){
                    Log.i("RecipesResponse", response.body().getCount()+"");
                    List<Hit> hits = response.body().getHits();
                    for (Hit hit : hits){
                        if(hit != null) {
                            if(hit.getRecipe() != null) {
                                Recipe r = hit.getRecipe();

                                MyRecipes recipes = new MyRecipes();
                                recipes.setIngredientCount(r.getIngredientLines().size());
                                recipes.setCalories(r.getCalories());
                                recipes.setImage(r.getImage());
                                recipes.setLabel(r.getLabel());
                                recipes.setShareAs(r.getShareAs());
                                recipes.setSource(r.getSource());
                                recipes.setTotalWeight(r.getTotalWeight());
                                recipes.setUri(r.getUri());
                                recipes.setUrl(r.getUrl());
                                recipes.setYield(r.getYield());
                                recipes.setCautions(r.getCautions()+"");
                                recipes.setHealthLabels(r.getHealthLabels()+"");
                                recipes.setDietLabels(r.getDietLabels()+"");
                                recipes.setIngredientLines(r.getIngredientLines()+"");

                                recipeList.add(recipes);
                            }
                        }
                    }

                    adapter.addAll(recipeList);
                    loading.setVisibility(View.GONE);
                    //hideProgressDialog();
                }else {
                    showSnackBar(recyclerView,"Failed to retrieve Recipes");
                    Log.i("GetOnlineRecipe", response.toString());
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                loading.setVisibility(View.GONE);
                //hideProgressDialog();
                Log.e("Err", t.toString());
                showSnackBar(recyclerView, "There was an error connecting to API");
            }
        });
    }

    //Retrieve User favourite Recipe from internal API
    private void getMyFavourite() {
        setTitle("My Favourites");
        loading.setVisibility(View.VISIBLE);
       // showProgressDialog(getString(R.string.fetching_favourite_recipe));
        recipeList = new ArrayList<>();
        recipeList = db.getMyFavourite();
        if(recipeList != null && recipeList.size()> 0){
            adapter.addAll(recipeList);
        }else{
            showToast("No Favourite Found");
        }
        loading.setVisibility(View.GONE);
       // hideProgressDialog();
    }

    void goBack() {
        Intent upIntent = new Intent(this, MainActivity.class);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities(ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppController.getInstance().getNavFragment().equals("R")) {
            getMenuInflater().inflate(R.menu.menu_recipes, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    AppController.getInstance().searchKey = query;
                    if(query.equalsIgnoreCase("sick")){
                        AppController.getInstance().searchKey = "for sickness";
                    }
                    getRecipeFromEdamam(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    void loadUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            goBack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable()) {
            if (AppController.getInstance().getNavFragment() != null && !AppController.getInstance().getNavFragment().isEmpty()) {
                String f = AppController.getInstance().getNavFragment();
                if (f.equals("R")) {
                    getRecipeFromEdamam(AppController.getInstance().searchKey);
                } else {
                    getMyFavourite();
                }
            } else {
                goBack();
            }
        } else {
            Snackbar.make(recyclerView, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
        }
    }
}
