package com.example.cookbook.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.repository.FavouriteRepository;
import java.util.List;

public class FavouriteViewModel extends AndroidViewModel {
    private FavouriteRepository mRepository;
    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        mRepository = FavouriteRepository.getInstance(application);
    }
    public LiveData<List<Recipe>> fetchRecipe() {
        return mRepository.loadRecipe();
    }

    public void unlike(String recipeID) {
        mRepository.removeFromFavorites(recipeID);
    }
    public void like(String recipeID) {
        mRepository.addToFavourite(recipeID);
    }
}
