package com.example.recipeapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Models.Recipe;
import com.example.recipeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder> {
    Context context;
    List<Recipe> list;
    RecipeClickListener listener;
    OnFavoriteToggleListener favoriteToggleListener;

    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.favoriteToggleListener = favoriteToggleListener;
    }


    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        Recipe recipe = list.get(position);
        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);
        holder.textView_servings.setText(recipe.servings > 0 ? recipe.servings + " Servings" : "Servings: N/A");
        holder.textView_like.setText(recipe.aggregateLikes > 0 ? recipe.aggregateLikes + " Likes" : "Likes: N/A");
        holder.textView_time.setText(recipe.readyInMinutes > 0 ? recipe.readyInMinutes + " mins" : "Time: N/A");


        Picasso.get().load(recipe.image).into(holder.imageView_Food);

        holder.random_list_container.setOnClickListener(v ->
                listener.onRecipeClicked(String.valueOf(recipe.id))
        );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            String userId = user.getUid();
            String recipeId = String.valueOf(recipe.id);

            db.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(recipeId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            holder.imageFavorite.setImageResource(R.drawable.fav);
                            holder.imageFavorite.setTag("favorited");
                        } else {
                            holder.imageFavorite.setImageResource(R.drawable.heart);
                            holder.imageFavorite.setTag("not_favorited");
                        }
                    });

            holder.imageFavorite.setOnClickListener(v -> {
                Object tag = holder.imageFavorite.getTag();
                if (tag != null && tag.equals("favorited")) {
                    removeFromFavorites(context, recipeId, holder.imageFavorite);
                } else {
                    addToFavorites(context, recipe, holder.imageFavorite);
                }
            });

        }
    }
    private void addToFavorites(Context context, Recipe recipe, ImageView imageFavorite) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String recipeId = String.valueOf(recipe.id);

            Map<String, Object> recipeMap = new HashMap<>();
            recipeMap.put("id", recipeId);
            recipeMap.put("title", recipe.title);
            recipeMap.put("image", recipe.image);
            recipeMap.put("servings", recipe.servings);
            recipeMap.put("aggregateLikes", recipe.aggregateLikes);
            recipeMap.put("readyInMinutes", recipe.readyInMinutes);

            db.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(recipeId)
                    .set(recipeMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Recipe added to favorites!", Toast.LENGTH_SHORT).show();
                        imageFavorite.setImageResource(R.drawable.fav);
                        imageFavorite.setTag("favorited");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to save recipe", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }


    private void removeFromFavorites(Context context, String recipeId, ImageView imageFavorite) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            db.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(recipeId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        imageFavorite.setImageResource(R.drawable.heart);
                        imageFavorite.setTag("not_favorited");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to remove recipe", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteRemoved(String recipeId);
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
class RandomRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title, textView_servings, textView_like, textView_time, textView_favourite;
    ImageView imageView_Food, imageFavorite;

    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_like = itemView.findViewById(R.id.textView_like);
        textView_time = itemView.findViewById(R.id.textView_time);
        textView_favourite = itemView.findViewById(R.id.textView_favourite);
        imageView_Food = itemView.findViewById(R.id.imageView_Food);
        imageFavorite = itemView.findViewById(R.id.image_favorite); // ✅ FIXED
    }
}
