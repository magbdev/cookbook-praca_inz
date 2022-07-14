package com.example.cookbook.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingRepository implements Repository {
    private static ShoppingRepository instance;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MutableLiveData<List<String>> shoppingList = new MediatorLiveData<>();
    private String docID;

    public static ShoppingRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ShoppingRepository(context);
        }
        return instance;
    }

    private ShoppingRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void fetchDataFromServer() {
        ArrayList<String> shopping = new ArrayList<>();
        CollectionReference ref = db.collection("users");
        ref.whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        docID = document.getId();
                        shopping.addAll((Collection<? extends String>) document.get("shoppingList"));
                    }
                    shoppingList.setValue(shopping);
                }
            }
        });

    }

    public LiveData<List<String>> loadShopping() {
        fetchDataFromServer();
        return shoppingList;
    }

    public void refreshShoppingList(ArrayList<String> list) {
        Map<String, Object> data = new HashMap<>();
        data.put("shoppingList",list);
        CollectionReference docRef = db.collection("users");
        docRef.document(docID).set(data, SetOptions.merge());
    }
}
