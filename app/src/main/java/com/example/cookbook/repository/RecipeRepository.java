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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RecipeRepository implements Repository{
    private static RecipeRepository instance;
    private Context mContext;
    private MutableLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();
    private MutableLiveData<List<Recipe>> myRecipes = new MediatorLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<String> recipeIDList;
    String docID;

    public static RecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context) {
        this.mContext = context.getApplicationContext();
        recipeIDList = new ArrayList<>();
    }

    public void fetchDataFromServer() {
        List<Recipe> recipeList = new ArrayList<>();
        CollectionReference docRef = db.collection("recipes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Recipe recipe= document.toObject(Recipe.class);
                        recipe.setRecipeId(document.getId());
                        recipeList.add(recipe);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                getFavourites(recipeList);
                }
            }
    );
    }
    public LiveData<List<Recipe>> loadRecipe() {
        fetchDataFromServer();
        return mRecipes;
    }

    public void addRecipe(Recipe recipe){
        db = FirebaseFirestore.getInstance();
        CollectionReference recipes = db.collection("recipes");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("title", recipe.getTitle());
        data1.put("description", recipe.getDescription());
        data1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data1.put("steps", recipe.getSteps());
        data1.put("ingredients",recipe.getIngredients());
        data1.put("imageUrl",recipe.getImageUrl());
        recipes.add(data1);
    }

    public void addRecipeToFavourite(String recipeID) {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("recipeID",recipeID);
        favorite.put("userID",auth.getCurrentUser().getUid());
        db.collection("favourites").add(favorite);
    }

    public void getFavourites(List<Recipe> list) {
        CollectionReference docRef = db.collection("favourites");
        docRef.whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        recipeIDList.add((String) document.get("recipeID"));
                    }
                    checkIfFavourite(list);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }

    public void checkIfFavourite(List<Recipe> list){
        for (Recipe r: list) {
            for (String id: recipeIDList) {
                if(r.getRecipeId().equals(id)) {
                    r.setFavourite(true);
                }
            }
        }
        recipeIDList.clear();
        mRecipes.setValue(list);
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
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public LiveData<List<Recipe>> getMyRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        CollectionReference docRef = db.collection("recipes");
        docRef.whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        recipeList.add(document.toObject(Recipe.class));
                    }
                   myRecipes.setValue(recipeList);
                }
            }
        });
        return myRecipes;
    }

}
