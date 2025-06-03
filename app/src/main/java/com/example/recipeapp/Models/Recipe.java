package com.example.recipeapp.Models;

import java.util.ArrayList;

public class Recipe {
    public int id;
    public String image;
    public String imageType;
    public String title;
    public int readyInMinutes;
    public int servings;
    public String sourceUrl;
    public boolean vegetarian;
    public boolean vegan;
    public boolean glutenFree;
    public boolean dairyFree;
    public boolean veryHealthy;
    public boolean cheap;
    public boolean veryPopular;
    public boolean sustainable;
    public boolean lowFodmap;
    public int weightWatcherSmartPoints;
    public String gaps;
    public Double preparationMinutes;
    public Double cookingMinutes;
    public int aggregateLikes;
    public double healthScore;
    public String creditsText;
    public String license;
    public String sourceName;
    public double pricePerServing;
    public ArrayList<ExtendedIngredient> extendedIngredients;
    public String summary;
    public ArrayList<Object> cuisines;
    public ArrayList<String> dishTypes;
    public ArrayList<String> diets;
    public ArrayList<Object> occasions;
    public String instructions;
    public ArrayList<AnalyzedInstruction> analyzedInstructions;
    public Integer originalId;
    public double spoonacularScore;
    public String spoonacularSourceUrl;
}
