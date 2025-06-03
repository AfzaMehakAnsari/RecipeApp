package com.example.recipeapp.Listeners;

public interface NutritionLabelListener {
    void didFetch(String htmlContent);
    void didError(String message);
}
