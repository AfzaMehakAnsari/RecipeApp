package com.example.recipeapp;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<blog_post> blogList;

    public BlogAdapter(List<blog_post> blogList) {
        this.blogList = blogList;
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlogViewHolder holder, int position) {
        blog_post blogPost = blogList.get(position);
        holder.title.setText(blogPost.getTitle());
        holder.description.setText(blogPost.getDescription());

        // Show/hide image
        if (blogPost.getImageUrl() != null && !blogPost.getImageUrl().isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            // 🔥 Use Glide or Picasso to load image
            Glide.with(holder.itemView.getContext())
                    .load(blogPost.getImageUrl())

                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blog_post.getLink()));
            v.getContext().startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blogTitle);
            description = itemView.findViewById(R.id.blogDescription);
            imageView = itemView.findViewById(R.id.blogImage);
        }
    }
}
