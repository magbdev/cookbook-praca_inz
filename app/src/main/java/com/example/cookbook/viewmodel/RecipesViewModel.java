package com.example.cookbook.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cookbook.model.Recipe;
import com.example.cookbook.repository.RecipeRepository;

import java.util.List;

public class RecipesViewModel extends AndroidViewModel {

    private RecipeRepository mRepository;

    public RecipesViewModel(Application application) {
        super(application);
        mRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<List<Recipe>> fetchRecipe() {
        return mRepository.loadRecipe();
    }

    public void like(String recipeID) {
        mRepository.addRecipeToFavourite(recipeID);
    }

    public void unlike(String recipeID) {
        mRepository.removeFromFavorites(recipeID);
    }
}
