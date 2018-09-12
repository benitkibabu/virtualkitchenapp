package com.cloudappdev.ben.virtualkitchen.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudappdev.ben.virtualkitchen.R;
import com.cloudappdev.ben.virtualkitchen.adapter.CustomIngredientRecyclerAdapter;
import com.cloudappdev.ben.virtualkitchen.app.AppConfig;
import com.cloudappdev.ben.virtualkitchen.app.AppController;
import com.cloudappdev.ben.virtualkitchen.base.BaseActivity;
import com.cloudappdev.ben.virtualkitchen.base.BaseAppCompatDialog;
import com.cloudappdev.ben.virtualkitchen.helper.AppPreference;
import com.cloudappdev.ben.virtualkitchen.helper.SQLiteHandler;
import com.cloudappdev.ben.virtualkitchen.main.MainActivity;
import com.cloudappdev.ben.virtualkitchen.models.Ingredient;
import com.cloudappdev.ben.virtualkitchen.models.UPCItem;
import com.cloudappdev.ben.virtualkitchen.models.UPCResponse;
import com.cloudappdev.ben.virtualkitchen.rest.APIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyIngredients extends BaseActivity {

    private RecyclerView recyclerView;
    private CustomIngredientRecyclerAdapter adapter;

    static List<Ingredient> myIngredients;

    AppPreference pref;
    SQLiteHandler db;

    FirebaseAuth fAuth;
    FirebaseUser user;
    LinearLayout loading;

    IntentIntegrator integrator;
    IntentResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

        setContentView(R.layout.activity_my_ingredients);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        pref = new AppPreference(this);
        db = new SQLiteHandler(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loading = findViewById(R.id.progressBar2);

        recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomIngredientRecyclerAdapter(this, R.layout.ingredient_item);
        recyclerView.setAdapter(adapter);


        if(db != null){
            getMyIngredient();
        }else{
            goBack();
        }

        adapter.setOnItemClickListener(new CustomIngredientRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                final Ingredient mI = myIngredients.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MyIngredients.this);
                builder.setTitle("DELETE?!");
                builder.setMessage("Do you want to remove this Item?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        removeIngredient(mI);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    private void showProgressBar(){
        loading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        loading.setVisibility(View.GONE);
    }


    @Override
    public void onResume(){
        super.onResume();
        if( isNetworkAvailable()){
            if(db != null){
                getMyIngredient();
            }else{
                goBack();
            }
        }else{
            Snackbar.make(recyclerView, "No internet connection", Snackbar.LENGTH_INDEFINITE)
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
        getMenuInflater().inflate(R.menu.menu_myingredients, menu);
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            //formatTxt.setText("FORMAT: " + scanFormat);
            //contentTxt.setText("CONTENT: " + scanContent);

            getProductByUPC(scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void getProductByUPC(String code){
        showProgressDialog("Findind Item!...");
        APIService service = AppConfig.getAPIService(AppConfig.UPLOOKUP);
        service.fetchUPCItem(code)
                .enqueue(new Callback<UPCResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UPCResponse> call, @NonNull Response<UPCResponse> response) {
                        if(response.isSuccessful()){
                            hideProgressDialog();
                            if(response.body()!= null && response.body().getItems() != null) {
                                List<UPCItem> upcItems = response.body().getItems();
                                Log.d("UPCResult", response.toString());
                                if (upcItems != null && upcItems.size() > 0) {
                                    ShowInsertDialog.newInstance(upcItems.get(0).getTitle()).show(getSupportFragmentManager(), "InsertDialog");
                                }
                            }else{
                                showToast("Item was not found");
                            }
                        }else{
                            showToast("You have added your ingredient");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UPCResponse> call, @NonNull Throwable t) {
                        Log.e("UPC_ITEM", t.toString());
                        hideProgressDialog();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            //Show Camera here for scanning items
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            return true;
        }

        else if (id == R.id.action_stores) {
            //Show maps activity
            Intent i = new Intent(MyIngredients.this, MapsActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == android.R.id.home){
            //goBack();
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMyIngredient(){
        showProgressBar();
        //showProgressDialog("Loading Items");
        myIngredients = new ArrayList<>();
        myIngredients = db.getMyIngredients();
        adapter.addAll(myIngredients);
        hideProgressBar();
        //hideProgressDialog();
    }

    public static class ShowInsertDialog extends AppCompatDialogFragment{
        private FirebaseUser user;
        private SQLiteHandler dbHandler;
        public static ShowInsertDialog newInstance(String title ){
            ShowInsertDialog fragment = new ShowInsertDialog();
            Bundle arg = new Bundle();
            arg.putString("code", title);
            fragment.setArguments(arg);
            return fragment;
        }

        public ShowInsertDialog(){}

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
//            Dialog dialog = super.onCreateDialog(savedInstanceState);
//            // request a window without the title
//            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            user = FirebaseAuth.getInstance().getCurrentUser();
            dbHandler = new SQLiteHandler(getContext());

            View view  = View.inflate(mContext, R.layout.dialog_add_ingredient, null);

            final TextView title = view.findViewById(R.id.title);
            Button saveBtn = view.findViewById(R.id.saveBtn);
            Button cancelBtn = view.findViewById(R.id.cancelBtn);

            if(getArguments() != null && !getArguments().isEmpty()) {
                title.setText(getArguments().getString("code"));
            }

            final EditText qField = view.findViewById(R.id.q_field);

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = title.getText().toString();
                    String quantity = qField.getText().toString().isEmpty() ? "1" : qField.getText().toString();

                    Ingredient ing = new Ingredient();
                    ing.setText(text);
                    ing.setWeight(Double.parseDouble(quantity));
                    ing.setUid(user.getUid());

                    addIngredient(ing);
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowInsertDialog.this.getDialog().cancel();
                }
            });

            builder.create().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//            builder.setIcon(R.mipmap.wine_bottle);
//            builder.setTitle(R.string.add_ingredient);
            builder.setCancelable(false);
            builder.setView(view);

            return builder.create();
        }
        void addIngredient(final Ingredient ingredient) {
            dbHandler.addIngredient(ingredient);
            ShowInsertDialog.this.getDialog().cancel();
        }
    }

    void removeIngredient(final Ingredient ingredient) {
       // showProgressDialog("Removing Item!...");
        showProgressBar();
        myIngredients = new ArrayList<>();
        int i = db.deleteIngredient(ingredient);
        if(i == 0){
            Snackbar.make(recyclerView, "Item Not Removed!", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(recyclerView, "Item Removed", Snackbar.LENGTH_LONG).show();
            getMyIngredient();
        }
        hideProgressBar();
       // hideProgressDialog();
    }
}
