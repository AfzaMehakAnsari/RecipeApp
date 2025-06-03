package com.example.recipeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Adapters.IngredientsSearchAdapter;
import com.example.recipeapp.Listeners.IngredientSearchListener;
import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Models.IngredientSearchRecipe;

import java.util.ArrayList;
import java.util.List;

public class IngredientSearchActivity extends AppCompatActivity {

    SearchView searchIngredientsView;
    RecyclerView recyclerViewIngredients;
    TextView textNoResults;
    ProgressDialog dialog;
    RequestManager manager;
    IngredientsSearchAdapter adapter;
    ImageView ingredientSearchIcon;

    List<String> ingredientsList = new ArrayList<>();

    private final IngredientSearchListener ingredientSearchListener = new IngredientSearchListener() {
        @Override
        public void didFetch(List<IngredientSearchRecipe> response, String message) {
            dialog.dismiss();

            if (response == null || response.isEmpty()) {
                recyclerViewIngredients.setVisibility(View.GONE);
                textNoResults.setVisibility(View.VISIBLE);
            } else {
                recyclerViewIngredients.setHasFixedSize(true);
                recyclerViewIngredients.setLayoutManager(new GridLayoutManager(IngredientSearchActivity.this, 1));
                adapter = new IngredientsSearchAdapter(IngredientSearchActivity.this, response, recipeClickListener);
                recyclerViewIngredients.setAdapter(adapter);

                recyclerViewIngredients.setVisibility(View.VISIBLE);
                textNoResults.setVisibility(View.GONE);
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(IngredientSearchActivity.this, message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };

    private final RecipeClickListener recipeClickListener = id -> {
        startActivity(new Intent(IngredientSearchActivity.this, RecipeDetailsActivity.class).putExtra("id", id));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_search);

        searchIngredientsView = findViewById(R.id.search_ingredients_view);
        recyclerViewIngredients = findViewById(R.id.recycler_ingredients);
        textNoResults = findViewById(R.id.text_no_results);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Searching Recipes...");

        manager = new RequestManager(this);

        searchIngredientsView.setVisibility(View.VISIBLE);
        searchIngredientsView.setIconified(false);
        searchIngredientsView.requestFocus();

        // Set yellow cursor on Android 10+
        EditText searchEditText = searchIngredientsView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchEditText.setTextCursorDrawable(R.drawable.yellow_cursor);
        }

        searchIngredientsView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ingredientsList.clear();
                String[] ingredients = query.split(",");
                for (String ingredient : ingredients) {
                    ingredientsList.add(ingredient.trim());
                }

                textNoResults.setVisibility(View.GONE);
                recyclerViewIngredients.setVisibility(View.GONE);
                manager.getRecipesByIngredients(ingredientSearchListener, ingredientsList);
                dialog.show();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
