package com.example.recipeapp;

public class blog_post {
    private String title;
    private String description;
    private static String link;
    private String imageUrl;

    public blog_post(String title, String description, String link, String imageUrl) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public static String getLink() { return link; }
    public String getImageUrl() { return imageUrl; }
}
