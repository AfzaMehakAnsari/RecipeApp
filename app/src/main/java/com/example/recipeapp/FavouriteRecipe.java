package com.example.recipeapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Adapters.RandomRecipeAdapter;
import com.example.recipeapp.Models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRecipe extends AppCompatActivity {

    RecyclerView recyclerView;
    RandomRecipeAdapter adapter;
    List<Recipe> favoriteList = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_recipe);

        recyclerView = findViewById(R.id.recycler_favorites);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadFavorites();
        } else {
            Toast.makeText(this, "Please log in to view favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFavorites() {
        db.collection("users")
                .document(user.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear();

                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        Recipe recipe = new Recipe();
                        recipe.id = Integer.parseInt(doc.getString("id"));
                        recipe.title = doc.getString("title");
                        recipe.image = doc.getString("image");

                        // Handle missing fields gracefully
                        recipe.servings = doc.getLong("servings") != null ? doc.getLong("servings").intValue() : -1;
                        recipe.aggregateLikes = doc.getLong("aggregateLikes") != null ? doc.getLong("aggregateLikes").intValue() : -1;
                        recipe.readyInMinutes = doc.getLong("readyInMinutes") != null ? doc.getLong("readyInMinutes").intValue() : -1;

                        favoriteList.add(recipe);
                    }

                    adapter = new RandomRecipeAdapter(this, favoriteList, recipeId -> {
                        // On recipe click
                        Toast.makeText(this, "Recipe ID: " + recipeId, Toast.LENGTH_SHORT).show();
                        // You can navigate to detail screen from here
                    });

                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch favorites", Toast.LENGTH_SHORT).show();
                });
    }
}
