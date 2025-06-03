package com.example.recipeapp.Models;

public class CuisineModel {
    private String name;
    private int imageResId;

    public CuisineModel(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }


}