package com.cloudappdev.ben.virtualkitchen.rest

import android.content.Context
import android.util.Log

import com.cloudappdev.ben.virtualkitchen.app.AppConfig
import com.cloudappdev.ben.virtualkitchen.app.AppController
import com.cloudappdev.ben.virtualkitchen.models.Ingredient

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Benit Kibabu on 30/05/2017.
 */

class IngredientService {

    var context: Context? = null

    private var added: Boolean = false
    private var removed: Boolean = false

    private var ingredientList: List<Ingredient>? = null

    fun addIngredient(ingredient: Ingredient): Boolean {
        added = false
        val service = AppConfig.getAPIService()
        service.addIngredient(AppController.getInstance().appKey(), ingredient)
                .enqueue(object : Callback<Ingredient> {
                    override fun onResponse(call: Call<Ingredient>, response: Response<Ingredient>) {
                        if (response.isSuccessful) {
                            added = true
                        }
                    }

                    override fun onFailure(call: Call<Ingredient>, t: Throwable) {
                        Log.e("NotSuccessful", t.toString())
                    }
                })

        return added
    }

    fun removeIngredient(ingredient: Ingredient): Boolean {
        removed = false
        val service = AppConfig.getAPIService()
        service.removeIngredient(ingredient.id, AppController.getInstance().appKey())
                .enqueue(object : Callback<Ingredient> {
                    override fun onResponse(call: Call<Ingredient>, response: Response<Ingredient>) {
                        if (response.isSuccessful) {
                            removed = true
                        } else {
                            Log.e("NotSuccessful", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<Ingredient>, t: Throwable) {
                        Log.e("NotSuccessful", t.toString())
                    }
                })

        return removed
    }

    fun retrieveIngredients(userID: Int): List<Ingredient> {
        ingredientList = ArrayList()
        val service = AppConfig.getAPIService()
        service.fetchMyIngredient(AppController.getInstance().appKey(), userID)
                .enqueue(object : Callback<List<Ingredient>> {
                    override fun onResponse(call: Call<List<Ingredient>>, response: Response<List<Ingredient>>) {
                        if (response.isSuccessful) {
                            ingredientList = response.body()
                            Log.d("Result", ingredientList!!.size.toString() + "")
                        }
                    }

                    override fun onFailure(call: Call<List<Ingredient>>, t: Throwable) {
                        Log.e("GetMyIngredients", t.toString())
                    }
                })

        return ingredientList as ArrayList<Ingredient>
    }

    companion object {


        fun getInstance(context: Context): IngredientService {
            val ingredientService = IngredientService()
            ingredientService.context = context
            return ingredientService
        }
    }

}
