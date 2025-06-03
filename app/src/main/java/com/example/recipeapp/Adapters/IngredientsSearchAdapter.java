package com.example.recipeapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Models.IngredientSearchRecipe;
import com.example.recipeapp.R;

import java.util.List;

public class IngredientsSearchAdapter extends RecyclerView.Adapter<IngredientsSearchViewHolder> {

    Context context;
    List<IngredientSearchRecipe> list;
    RecipeClickListener listener;

    public IngredientsSearchAdapter(Context context, List<IngredientSearchRecipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientsSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientsSearchViewHolder(LayoutInflater.from(context).inflate(R.layout.list_ingredient_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsSearchViewHolder holder, int position) {
        IngredientSearchRecipe recipe = list.get(position);

        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);
        holder.textView_used.setText("Used: " + recipe.usedIngredientCount);
        holder.textView_missed.setText("Missed: " + recipe.missedIngredientCount);

        Glide.with(context).load(recipe.image).into(holder.imageView_food);

        holder.card_holder.setOnClickListener(v -> {
            listener.onRecipeClicked(String.valueOf(recipe.id));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class IngredientsSearchViewHolder extends RecyclerView.ViewHolder {
    CardView card_holder;
    TextView textView_title, textView_used, textView_missed;
    ImageView imageView_food;

    public IngredientsSearchViewHolder(@NonNull View itemView) {
        super(itemView);
        card_holder = itemView.findViewById(R.id.card_holder);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_used = itemView.findViewById(R.id.textView_used);
        textView_missed = itemView.findViewById(R.id.textView_missed);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}
