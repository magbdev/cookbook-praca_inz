package com.example.cookbook.model;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Recipe implements Parcelable {

    private String recipeId;
    private String title;
    private String description;
    private String imageUrl;
    private HashMap<String,String> ingredients;
    private ArrayList<String> steps;
    private Boolean isFavourite = false;
    private String userID;

    public Recipe(){};
    public Recipe(String recipeId, String title, String description,String imageUrl, HashMap<String,String> ingredients, ArrayList<String> steps,String userID) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.steps = steps;
        this.userID = userID;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    protected Recipe(Parcel in){
        recipeId = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        ingredients = (HashMap<String, String>) in.readSerializable();
        steps = new ArrayList<>();
        in.readStringList(steps);
        userID = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeSerializable(ingredients);
        dest.writeStringList(steps);
        dest.writeString(userID);
    }
}
