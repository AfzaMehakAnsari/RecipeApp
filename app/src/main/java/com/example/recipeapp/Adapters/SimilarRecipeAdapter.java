package com.example.recipeapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Listeners.RecipeClickListener;
import com.example.recipeapp.Models.SimilarRecipeResponse;
import com.example.recipeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarRecipeAdapter extends RecyclerView.Adapter<SimilarRecipeViewHolder>{
    Context context;
    List<SimilarRecipeResponse> list;
    RecipeClickListener listener;

    public SimilarRecipeAdapter(Context context, List<SimilarRecipeResponse> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @androidx.annotation.NonNull
    @Override
    public SimilarRecipeViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new SimilarRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_simliar_recipes,parent,false));
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull SimilarRecipeViewHolder holder, int position) {
    holder.textView_Similar_title.setText(list.get(position).title);
    holder.textView_Similar_title.setSelected(true);
    holder.textView_Similar_serving.setText(list.get(position).servings+"Persons");
        Picasso.get().load( "https://img.spoonacular.com/recipes/"+list.get(position).id+"-556x370."+list.get(position).imageType).into(holder.imageView_similar);
        holder.similar_recipe_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class SimilarRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView similar_recipe_holder;
    TextView textView_Similar_title, textView_Similar_serving;
    ImageView imageView_similar;
    public SimilarRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        similar_recipe_holder=itemView.findViewById(R.id.similar_recipe_holder);
        textView_Similar_title=itemView.findViewById(R.id.textView_Similar_title);
        textView_Similar_serving=itemView.findViewById(R.id.textView_Similar_serving);
        imageView_similar=itemView.findViewById(R.id.imageView_similar);

    }
}
