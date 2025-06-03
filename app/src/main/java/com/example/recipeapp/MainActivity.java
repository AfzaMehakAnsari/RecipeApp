package com.example.recipeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.recipeapp.R; // Your actual app package

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Adapters.RandomRecipeAdapter;
import com.example.recipeapp.Listeners.RandomRecipeResponseListener;
import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Models.CuisineModel;
import com.example.recipeapp.Models.RandomRecipesApiResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProgressDialog dialog;
    RequestManager manager;
    RecyclerView recyclerView;
    RandomRecipeAdapter adapter;
    SearchView searchView;
    List<String> tags = new ArrayList<>();

    BottomNavigationView bottomNavigationView;
   // ImageView cooking, line;
    RecyclerView cuisineRecycler;
    List<CuisineModel> cuisineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabChatbot = findViewById(R.id.fab_chatbot);
        fabChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, chatbot.class);
            startActivity(intent);
        });

        FloatingActionButton fabblog = findViewById(R.id.fab_blog);
        fabblog.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, blog.class);
            startActivity(intent);
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //  cooking = findViewById(R.id.cooking_pic);
        //  line = findViewById(R.id.line_pic);

//        // Animate left image
//        TranslateAnimation animation = new TranslateAnimation(-500, 0, 0, 0);
//        animation.setDuration(1000);
//        animation.setFillAfter(true);
//        line.startAnimation(animation);
//
//        // Animate right image
//        TranslateAnimation animationRight = new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, 1.0f,
//                Animation.RELATIVE_TO_PARENT, 0f,
//                Animation.ABSOLUTE, 0,
//                Animation.ABSOLUTE, 0);
//        animationRight.setDuration(1000);
//        animationRight.setFillAfter(true);
//        cooking.startAnimation(animationRight);

        navigationView.setNavigationItemSelectedListener(item -> {
            String selectedTag = item.getTitle().toString();
            tags.clear();
            tags.add(selectedTag);
            manager.getRandomRecipes(randomRecipeResponselistener, tags);
            dialog.show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                tags.clear();
                manager.getRandomRecipes(randomRecipeResponselistener, tags);
                dialog.show();
                return true;

            } else if (itemId == R.id.nav_ingredient) {
                Toast.makeText(this, "Search By Ingredients Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, IngredientSearchActivity.class));
                return true;

            } else if (itemId == R.id.nav_favorite) {
                Intent intent = new Intent(MainActivity.this, FavouriteRecipe.class);
                startActivity(intent);

                return true;

            } else if (itemId == R.id.nav_menu) {
                Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            else if (itemId == R.id.nav_logout) {
                // Logout logic here
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

                // Example: FirebaseAuth
                FirebaseAuth.getInstance().signOut();

                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        manager = new RequestManager(this);
        searchView = findViewById(R.id.search_View);
        searchView.findViewById(androidx.appcompat.R.id.search_plate).setBackgroundColor(android.graphics.Color.TRANSPARENT);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                tags.clear();
//                tags.add(query);
//                manager.getRandomRecipes(randomRecipeResponselistener, tags);
//                dialog.show();
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        // Initially load recipes
        tags.clear();
        manager.getRandomRecipes(randomRecipeResponselistener, tags);
        dialog.show();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, login.class));
            finish();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.show();
                drawerLayout.closeDrawer(GravityCompat.START);
                saveSearchQueryToFirestore(query); // 👈 Call to save history
                tags.clear();
                tags.add(query);
                manager.getRandomRecipes(randomRecipeResponselistener, tags);
                dialog.show();
                drawerLayout.closeDrawer(GravityCompat.START);

                manager.getRecipesBySearch(query, new RandomRecipeResponseListener() {
                    @Override
                    public void didFetch(RandomRecipesApiResponse response, String message) {
                        recyclerView = findViewById(R.id.recyclerrandom);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                        adapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void didError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                fetchSearchHistory();
            }
        });

    }

    private void saveSearchQueryToFirestore(String query) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("FIRESTORE", "User not logged in");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("query", query);
        data.put("timestamp", new Timestamp(new Date())); // Use Firestore Timestamp

        db.collection("users")
                .document(user.getUid())
                .collection("search_history")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FIRESTORE", "Query saved: " + query);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Failed to save query", e);
                });
    }


    private void fetchSearchHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("search_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> suggestions = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        suggestions.add(doc.getString("query"));
                    }
                    showSuggestions(suggestions);
                });
    }

    private void showSuggestions(List<String> suggestions) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );

        AutoCompleteTextView autoComplete = (AutoCompleteTextView) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        autoComplete.setAdapter(adapter);

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedQuery = adapter.getItem(position);
            searchView.setQuery(selectedQuery, false); // set query in search bar
        });
    }

    private void setupCuisineClickListeners() {
        int[] cuisineIds = {
                R.id.italian_circle,
                R.id.korean_circle,
                R.id.european_circle,
                R.id.asian_circle,
                R.id.chinese_circle,
                R.id.japanese_circle,
                R.id.thai_circle,
                R.id.spanish_circle,
                R.id.british_circle,
                R.id.mediterranean_circle
        };

        String[] cuisineNames = {
                "Italian",
                "Korean",
                "European",
                "Asian",
                "Chinese",
                "Japanese",
                "Thai",
                "Spanish",
                "British",
                "Mediterranean"
        };

        for (int i = 0; i < cuisineIds.length; i++) {
            int finalI = i;
            findViewById(cuisineIds[i]).setOnClickListener(v -> {
                fetchCuisineRecipes(cuisineNames[finalI]);
            });
        }
    }

    private void fetchCuisineRecipes(String cuisine) {
        dialog.show();
        manager.getRecipesByCuisine(randomRecipeResponselistener, cuisine);
    }


    private final RandomRecipeResponseListener randomRecipeResponselistener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipesApiResponse response, String message) {
            recyclerView = findViewById(R.id.recyclerrandom);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            adapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(adapter);
            dialog.dismiss();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };

    private final RecipeClickListener recipeClickListener = id ->
            startActivity(new Intent(MainActivity.this, RecipeDetailsActivity.class).putExtra("id", id));

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}