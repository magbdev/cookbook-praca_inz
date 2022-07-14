package com.example.cookbook.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.repository.RecipeRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddRecipeViewModel extends AndroidViewModel {
    public MutableLiveData<String> Title = new MutableLiveData<>();
    public MutableLiveData<String> Description = new MutableLiveData<>();
    public MutableLiveData<String> ImageUrl = new MutableLiveData<>();
    public MutableLiveData<HashMap<String,String>> Ingredient = new MutableLiveData<>();
    public MutableLiveData<List<String>> Step = new MutableLiveData<>();
    private MutableLiveData<Recipe> recipeMutableLiveData;

    public AddRecipeViewModel(Application application) {
        super(application);
        repo = RecipeRepository.getInstance(application);
    }

    private RecipeRepository repo;

    public MutableLiveData<Recipe> getRecipe() {

        if (recipeMutableLiveData == null) {
            recipeMutableLiveData = new MutableLiveData<>();
        }
        return recipeMutableLiveData;
    }

    public void onClick(View view) {
        Recipe recipe = new Recipe();
        recipe.setTitle(Title.getValue());
        recipe.setDescription(Description.getValue());
        recipe.setImageUrl(ImageUrl.getValue());
        recipe.setIngredients(Ingredient.getValue());
        recipe.setSteps((ArrayList<String>) Step.getValue());
        recipeMutableLiveData.setValue(recipe);
    }


    public void sendRecipe(Uri filePath,Recipe recipe){
        if(filePath != null){
            StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child(recipe.getTitle());
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = storageRef.getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageReference = uri.toString();
                            recipe.setImageUrl(imageReference);
                            repo.addRecipe(recipe);
                        }
                    });
                }
            });
        }
    }


}
