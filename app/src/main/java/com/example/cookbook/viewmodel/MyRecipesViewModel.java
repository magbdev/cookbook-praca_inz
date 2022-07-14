package com.example.cookbook.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cookbook.model.Recipe;
import com.example.cookbook.repository.RecipeRepository;

import java.util.List;

public class MyRecipesViewModel extends AndroidViewModel {

    private RecipeRepository mRepository;

    public MyRecipesViewModel(@NonNull Application application) {
        super(application);
        mRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<List<Recipe>> fetchRecipe() {
          return mRepository.getMyRecipes();
    }

}
