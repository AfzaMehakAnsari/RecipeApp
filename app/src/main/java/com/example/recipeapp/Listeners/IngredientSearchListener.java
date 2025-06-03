package com.example.recipeapp.Listeners;

import com.example.recipeapp.Models.IngredientSearchRecipe;

import java.util.List;

public interface IngredientSearchListener {
    void didFetch(List<IngredientSearchRecipe> response, String message);
    void didError(String message);
}
