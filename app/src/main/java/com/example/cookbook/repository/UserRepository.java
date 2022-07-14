package com.example.cookbook.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cookbook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository implements Repository{
    private static UserRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<User> user = new MediatorLiveData<>();

    public static UserRepository getInstance() {
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    @Override
    public void fetchDataFromServer() {
    }

    public LiveData<User> getUser(String userID) {
        CollectionReference docRef = db.collection("users");
        docRef.whereEqualTo("userID",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user.setValue(document.toObject(User.class));
                    }
                }
            }
        });
        return user;
    }

}
