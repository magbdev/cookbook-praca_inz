package com.example.cookbook.repository;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cookbook.model.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FavouriteRepository implements Repository{
    private static FavouriteRepository instance;
    private Context mContext;
    private MutableLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<String> recipeIDList;
    String docID;


    public static FavouriteRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FavouriteRepository(context);
        }
        return instance;
    }

    private FavouriteRepository(Context context) {
        this.mContext = context.getApplicationContext();
        recipeIDList = new ArrayList<>();
    }

    public void fetchDataFromServer() {
        CollectionReference docRef = db.collection("favourites");
        docRef.whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        recipeIDList.add((String) document.get("recipeID"));
                    }
                    getRecipeData();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }


    public void getRecipeData() {
        List<Recipe> recipeList = new ArrayList<>();
        for (String i: recipeIDList
             ) {
            DocumentReference docRef = db.collection("recipes").document(i);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Recipe recipe= document.toObject(Recipe.class);
                        recipe.setRecipeId(i);
                        recipe.setFavourite(true);
                        recipeList.add(recipe);
                    }
                    else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    mRecipes.setValue(recipeList);
                    recipeList.clear();
                    recipeIDList.clear();
                }
            });
        }
        if(recipeIDList.isEmpty()) {
            mRecipes.postValue(recipeList);
        }
    }

    public LiveData<List<Recipe>> loadRecipe() {
        fetchDataFromServer();
        return mRecipes;
    }

    public void removeFromFavorites(String recipeID) {
        CollectionReference docRef = db.collection("favourites");
        docRef.whereEqualTo("recipeID",recipeID).whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        docID = document.getId();
                    }
                    remove(docID);
                }
            }
        });

    }

    public void remove(String docID) {
        db.collection("favourites").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                fetchDataFromServer();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public void addToFavourite(String recipeID) {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("recipeID",recipeID);
        favorite.put("userID",auth.getCurrentUser().getUid());
        db.collection("favourites").add(favorite);
    }

}
