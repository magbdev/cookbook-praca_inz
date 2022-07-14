package com.example.cookbook.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cookbook.model.User;
import com.example.cookbook.repository.UserRepository;

public class RecipeDetailsViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    public RecipeDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance();
    }

    public LiveData<User> fetchData(String userID) {
        return mRepository.getUser(userID);
    }


}
