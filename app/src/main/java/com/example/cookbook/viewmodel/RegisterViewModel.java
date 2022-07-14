package com.example.cookbook.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cookbook.model.LoginUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {
    public MutableLiveData<String> EmailAddress = new MutableLiveData<>();
    public MutableLiveData<String> Password = new MutableLiveData<>();
    public MutableLiveData<String> FirstName = new MutableLiveData<>();
    public MutableLiveData<String> LastName = new MutableLiveData<>();
    private MutableLiveData<LoginUser> userMutableLiveData;
    private FirebaseFirestore db;

    public MutableLiveData<LoginUser> getUser() {

        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;

    }

    public void onClick(View view) {
        LoginUser loginUser = new LoginUser(EmailAddress.getValue(), Password.getValue(),FirstName.getValue(),LastName.getValue(),null);

        userMutableLiveData.setValue(loginUser);
    }

    public void addUserToDatabase(LoginUser user) {
        db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("firstName", user.getFirstName());
        data1.put("lastName", user.getLastName());
        data1.put("userID", user.getUserID());
        users.add(data1);
    }
}
