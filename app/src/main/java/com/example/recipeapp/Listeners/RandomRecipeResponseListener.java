package com.example.recipeapp.Listeners;

import com.example.recipeapp.Models.RandomRecipesApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipesApiResponse response, String message);
    void didError(String message);
}
