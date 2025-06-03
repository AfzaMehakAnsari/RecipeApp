package com.example.recipeapp;

import android.content.Context;

import com.example.recipeapp.Listeners.*;
import com.example.recipeapp.Models.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;

public class RequestManager {
    private static final String BASE_URL = "https://api.spoonacular.com/";
    private static final String API_KEY = "1cfa4a4aaf3c4e6b880985a36743bdbf";

    private final Retrofit retrofit;

    public RequestManager(Context context) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getRandomRecipes(RandomRecipeResponseListener listener, List<String> tags) {
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        String tagsString = (tags != null && !tags.isEmpty()) ? String.join(",", tags) : "";

        Call<RandomRecipesApiResponse> call = callRandomRecipes.callRandomRecipe(API_KEY, "10", tagsString);
        call.enqueue(new Callback<RandomRecipesApiResponse>() {
            @Override
            public void onResponse(Call<RandomRecipesApiResponse> call, Response<RandomRecipesApiResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRecipesApiResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRecipesBySearch(String query, RandomRecipeResponseListener listener) {
        CallRecipesBySearch callService = retrofit.create(CallRecipesBySearch.class);
        Call<CuisineSearchResponse> call = callService.searchRecipes(query, "10",true, API_KEY);

        call.enqueue(new Callback<CuisineSearchResponse>() {
            @Override
            public void onResponse(Call<CuisineSearchResponse> call, Response<CuisineSearchResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.didError(response.message());
                    return;
                }

                if (response.body().results == null || response.body().results.isEmpty()) {
                    // No results found, send empty response
                    listener.didFetch(new RandomRecipesApiResponse(), "No results found");
                    return;
                }

                RandomRecipesApiResponse mockResponse = new RandomRecipesApiResponse();
                mockResponse.recipes = new ArrayList<>();

                for (RecipeDetailsResponse item : response.body().results) {
                    Recipe recipe = new Recipe();

                    recipe.id = item.id;
                    recipe.title = item.title;
                    recipe.image = item.image;
                    recipe.readyInMinutes = item.readyInMinutes;
                    recipe.servings = item.servings;
                    recipe.vegetarian = item.vegetarian;
                    recipe.veryHealthy = item.veryHealthy;
                    recipe.summary = item.summary;
                    recipe.aggregateLikes=item.aggregateLikes;

                    mockResponse.recipes.add(recipe);
                }
                listener.didFetch(mockResponse, response.message());
            }

            @Override
            public void onFailure(Call<CuisineSearchResponse> call, Throwable t) {
                listener.didError("Spoonacular request failed: " + t.getMessage());
            }
        });
    }
    public void getRecipesByCuisine(RandomRecipeResponseListener listener, String cuisine) {
        CallRecipesByCuisine callService = retrofit.create(CallRecipesByCuisine.class);
        Call<CuisineSearchResponse> call = callService.getRecipesByCuisine(cuisine, "10", true, API_KEY);

        call.enqueue(new Callback<CuisineSearchResponse>() {
            @Override
            public void onResponse(Call<CuisineSearchResponse> call, Response<CuisineSearchResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    listener.didError(response.message());
                    return;
                }

                RandomRecipesApiResponse mockResponse = new RandomRecipesApiResponse();
                mockResponse.recipes = new ArrayList<>();

                for (RecipeDetailsResponse item : response.body().results) {
                    Recipe recipe = new Recipe();

                    recipe.id = item.id;
                    recipe.title = item.title;
                    recipe.image = item.image;
                    recipe.readyInMinutes = item.readyInMinutes;
                    recipe.servings = item.servings;
                    recipe.vegetarian = item.vegetarian;
                    recipe.veryHealthy = item.veryHealthy;
                    recipe.summary = item.summary;
                    recipe.aggregateLikes = item.aggregateLikes;  // Ye ab mil jayega!

                    mockResponse.recipes.add(recipe);
                }
                listener.didFetch(mockResponse, response.message());
            }

            @Override
            public void onFailure(Call<CuisineSearchResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRecipeDetails(RecipeDetailsListener listener, int id) {
        CallRecipeDetails service = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = service.callRecipeDetails(id, API_KEY);
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getSimilarRecipes(SimilarRecipeListener listener, int id) {
        CallSimilarRecipes service = retrofit.create(CallSimilarRecipes.class);
        Call<List<SimilarRecipeResponse>> call = service.callSimilarRecipes(id, "4", API_KEY);
        call.enqueue(new Callback<List<SimilarRecipeResponse>>() {
            @Override
            public void onResponse(Call<List<SimilarRecipeResponse>> call, Response<List<SimilarRecipeResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<SimilarRecipeResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getInstructions(InstructionsListener listener, int id) {
        CallInstructions service = retrofit.create(CallInstructions.class);
        Call<List<InstructionsResponse>> call = service.callInstructions(id, API_KEY);

        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsResponse>> call, Response<List<InstructionsResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRecipesByIngredients(IngredientSearchListener listener, List<String> ingredients) {
        CallRecipesByIngredients service = retrofit.create(CallRecipesByIngredients.class);
        String joinedIngredients = String.join(",", ingredients);

        try {
            joinedIngredients = URLEncoder.encode(joinedIngredients, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            listener.didError("Encoding Error");
            return;
        }

        Call<List<IngredientSearchRecipe>> call = service.callRecipesByIngredients(joinedIngredients, "10", API_KEY);
        call.enqueue(new Callback<List<IngredientSearchRecipe>>() {
            @Override
            public void onResponse(Call<List<IngredientSearchRecipe>> call, Response<List<IngredientSearchRecipe>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<IngredientSearchRecipe>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getNutritionLabel(int id, NutritionLabelListener listener) {
        CallNutritionLabel service = retrofit.create(CallNutritionLabel.class);
        Call<String> call = service.callNutritionLabel(id, API_KEY, true);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    // Retrofit API interfaces

    private interface CallRandomRecipes {
        @GET("recipes/random")
        Call<RandomRecipesApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") String tags
        );
    }

    private interface CallRecipeDetails {
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallSimilarRecipes {
        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipeResponse>> callSimilarRecipes(
                @Path("id") int id,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallInstructions {
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>> callInstructions(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallRecipesByIngredients {
        @GET("recipes/findByIngredients")
        Call<List<IngredientSearchRecipe>> callRecipesByIngredients(
                @Query("ingredients") String ingredients,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallNutritionLabel {
        @GET("recipes/{id}/nutritionLabel")
        Call<String> callNutritionLabel(
                @Path("id") int id,
                @Query("apiKey") String apiKey,
                @Query("defaultCss") boolean defaultCss
        );
    }

    private interface CallRecipesByCuisine {
        @GET("recipes/complexSearch")
        Call<CuisineSearchResponse> getRecipesByCuisine(
                @Query("cuisine") String cuisine,
                @Query("number") String number,
                @Query("addRecipeInformation") boolean addRecipeInformation,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallRecipesBySearch {
        @GET("recipes/complexSearch")
        Call<CuisineSearchResponse> searchRecipes(
                @Query("query") String query,
                @Query("number") String number,
                @Query("addRecipeInformation") boolean addRecipeInformation,
                @Query("apiKey") String apiKey
        );
    }

}