package com.example.cookbook.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesHolder> implements Filterable {

    private LayoutInflater layoutInflater;
    private ArrayList<Recipe> data = new ArrayList<>();
    private OnRecipeListener onRecipeListener;
    private ArrayList<Recipe> dataFullList = new ArrayList<>();

    public RecipesAdapter(Context context, ArrayList<Recipe> data,ArrayList<Recipe> dataFullList,OnRecipeListener onRecipeListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.dataFullList = dataFullList;
        this.onRecipeListener = onRecipeListener;
    }

    public RecipesAdapter(Context context,OnRecipeListener onRecipeListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.onRecipeListener = onRecipeListener;
    }


    @NonNull
    @Override
    public RecipesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recipe_card, parent,false);
        return new RecipesHolder(view,onRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        if(data == null){
            return 0;
        }
        return data.size();
    }

    public List<String> getTitles() {
        List<String> list = new ArrayList<>();
        for (Recipe s: data) {
            list.add(s.getTitle());
        }
        return list;
    }
    public Recipe getObject(int index) {
        if(index<data.size()) {
            return data.get(index);
        }
        return null;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filterList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filterList.addAll(dataFullList);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Recipe item: dataFullList) {
                    if(item.getTitle().toLowerCase().contains(filterPattern)) {
                        filterList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public void addRecipeList(List<Recipe> recipes) {
        data.clear();
        data.addAll(recipes);
    }

    public void addRecipeFullList(List<Recipe> recipes) {
        dataFullList.clear();
        dataFullList.addAll(recipes);
//        dataFullList = (ArrayList<Recipe>) recipes;
    }

    public interface OnRecipeListener {
        void onRecipeClick(Recipe position);
        void onFavouriteClick(Recipe position);
    }

    public class RecipesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageView recipeImage;
        public ImageButton favouriteButton;
        public OnRecipeListener onRecipeListener;
        public RecipesHolder(View view, OnRecipeListener onRecipeListener){
            super(view);
            title = (TextView)view.findViewById(R.id.recipe_name_text);
            recipeImage = (ImageView)view.findViewById(R.id.recipeImage);
            favouriteButton = view.findViewById(R.id.favouriteButton);
            this.onRecipeListener = onRecipeListener;
            view.setOnClickListener(this);

        }
        public void bind(final Recipe recipe) {
            Glide.with(recipeImage.getContext()).load(recipe.getImageUrl()).into(recipeImage);
            title.setText(recipe.getTitle());
            if(recipe.getFavourite()) {
                favouriteButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
            }
            else{
                favouriteButton.clearColorFilter();
            }
            itemView.setOnClickListener(this);
            favouriteButton.setOnClickListener(v -> onRecipeListener.onFavouriteClick(data.get(getAdapterPosition())));
        }

        @Override
        public void onClick(View v) {
            onRecipeListener.onRecipeClick(data.get(getAdapterPosition()));
        }
    }
}
