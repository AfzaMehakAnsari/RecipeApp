package com.example.recipeapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Adapters.IngredientsAdapter;
import com.example.recipeapp.Adapters.InstructionsAdapter;
import com.example.recipeapp.Adapters.SimilarRecipeAdapter;
import com.example.recipeapp.Listeners.InstructionsListener;
import com.example.recipeapp.Listeners.NutritionLabelListener;
import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Listeners.RecipeDetailsListener;
import com.example.recipeapp.Listeners.SimilarRecipeListener;
import com.example.recipeapp.Models.InstructionsResponse;
import com.example.recipeapp.Models.RecipeDetailsResponse;
import com.example.recipeapp.Models.SimilarRecipeResponse;
import com.example.recipeapp.RequestManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    int id;
    TextView textView_meal_name, textView_meal_source;
    ImageView imageView_meal_image;
    RecyclerView recycler_meal_ingredients, recycler_meal_similar, recycler_meal_instructions;
    Button button_show_summary, button_show_nutrition;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;
    String meal_summary_text = "";

    // ✅ Field-level listener
    private final NutritionLabelListener nutritionLabelListener = new NutritionLabelListener() {
        @Override
        public void didFetch(String response) {
            dialog.dismiss();
            showNutritionDialog(response);
        }

        @Override
        public void didError(String message) {
            dialog.dismiss();
            Toast.makeText(RecipeDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        findViews();

        id = Integer.parseInt(getIntent().getStringExtra("id"));

        manager = new RequestManager(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.show();

        manager.getRecipeDetails(recipeDetailsListener, id);
        manager.getSimilarRecipes(similarRecipeListener, id);
        manager.getInstructions(instructionsListener, id);

        button_show_summary.setOnClickListener(v -> showSummaryDialog());

        // Use field listener directly
        button_show_nutrition.setOnClickListener(v -> {
            dialog.setTitle("Loading Nutrition...");
            dialog.show();
            manager.getNutritionLabel(id, nutritionLabelListener);
        });
    }

    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar = findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);
        button_show_summary = findViewById(R.id.button_show_summary);
        button_show_nutrition = findViewById(R.id.button_show_nutrition);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textView_meal_name.setText(response.title);
            textView_meal_source.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);

            meal_summary_text = response.summary;
        }

        @Override
        public void didError(String message) {
            dialog.dismiss();
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final SimilarRecipeListener similarRecipeListener = new SimilarRecipeListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetailsActivity.this, response, recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = id -> {
        Intent intent = new Intent(RecipeDetailsActivity.this, RecipeDetailsActivity.class);
        intent.putExtra("id", String.valueOf(id));
        startActivity(intent);
    };

    private void showSummaryDialog() {
        if (meal_summary_text == null || meal_summary_text.isEmpty()) {
            Toast.makeText(this, "Summary not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        String plainSummary = android.text.Html.fromHtml(meal_summary_text).toString();
        String dishName = textView_meal_name.getText().toString();
        plainSummary = plainSummary.replace(dishName, "").trim();
        plainSummary = plainSummary.replaceAll("\\s+", " ");

        TextView message = new TextView(this);
        message.setText(plainSummary);
        message.setPadding(50, 30, 50, 30);
        message.setTextColor(getResources().getColor(R.color.text_primary));
        message.setTextSize(16f);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recipe Summary");
        builder.setView(message);
        builder.setPositiveButton("Close", null);

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.background);
        }

        dialog.show();
    }

    private void showNutritionDialog(String htmlContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nutrition Information");

        WebView webView = new WebView(this);
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        builder.setView(webView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
