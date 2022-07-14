package com.example.cookbook.ui.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.recipes.RecipeDetailsActivity;
import com.example.cookbook.adapter.RecipesAdapter;
import com.example.cookbook.viewmodel.FavouriteViewModel;
import com.example.cookbook.viewmodel.RecipesViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment implements RecipesAdapter.OnRecipeListener {

    private FavouriteViewModel favouriteViewModel;
    private RecyclerView recyclerView;
    private RecipesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);
        favouriteViewModel =
                ViewModelProviders.of(this).get(FavouriteViewModel.class);

        recyclerView = root.findViewById(R.id.recyclerViewFavourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RecipesAdapter(this.getContext(), this);
        recyclerView.setAdapter(adapter);

        favouriteViewModel.fetchRecipe().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
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
        if(position.getFavourite() ){
            //unlike
            position.setFavourite(false);
            favouriteViewModel.unlike(position.getRecipeId());
        }
        else {
            //like
            favouriteViewModel.like(position.getRecipeId());
        }
    }
}