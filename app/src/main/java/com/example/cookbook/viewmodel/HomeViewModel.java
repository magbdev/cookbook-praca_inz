package com.example.cookbook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Co dzisiaj gotujemy? " +
                "Kliknij przycisk aby wyszukać przepis");
    }

    public LiveData<String> getText() {
        return mText;
    }
}