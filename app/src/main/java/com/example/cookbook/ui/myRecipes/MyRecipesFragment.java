package com.example.cookbook.ui.myRecipes;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookbook.R;
import com.example.cookbook.adapter.RecipesAdapter;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.recipes.RecipeDetailsActivity;
import com.example.cookbook.viewmodel.MyRecipesViewModel;
import com.example.cookbook.viewmodel.RecipesViewModel;

import java.util.List;


public class MyRecipesFragment extends Fragment implements RecipesAdapter.OnRecipeListener{

    private MyRecipesViewModel recipesViewModel;
    private static RecipesAdapter adapter;
    private static RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recipesViewModel =
                ViewModelProviders.of(this).get(MyRecipesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewMyRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RecipesAdapter(this.getContext(),this);
        recyclerView.setAdapter(adapter);

        recipesViewModel.fetchRecipe().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                adapter.addRecipeList(recipes);
                adapter.addRecipeFullList(recipes);
                adapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    @Override
    public void onRecipeClick(Recipe position) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra("recipe",position);
        startActivity(intent);
    }

    @Override
    public void onFavouriteClick(Recipe position) {

    }
}