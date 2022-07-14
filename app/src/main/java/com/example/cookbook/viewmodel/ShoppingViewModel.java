package com.example.cookbook.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cookbook.repository.ShoppingRepository;

import java.util.ArrayList;
import java.util.List;

public class ShoppingViewModel extends AndroidViewModel {
    private ShoppingRepository mRepository;

    public ShoppingViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShoppingRepository.getInstance(application);
    }

    public LiveData<List<String>> fetchRecipe() {
        return mRepository.loadShopping();
    }

    public void updateShoppingList(ArrayList<String> list) {
        mRepository.refreshShoppingList(list);
    }
}