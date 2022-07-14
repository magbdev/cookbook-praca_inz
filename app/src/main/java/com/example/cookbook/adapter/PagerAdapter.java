package com.example.cookbook.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.ui.recipes.RecipeInfoFragment;
import com.example.cookbook.ui.recipes.RecipeIngredientsFragment;
import com.example.cookbook.ui.recipes.RecipeStepsFragment;


public class PagerAdapter extends FragmentPagerAdapter {

    private Recipe recipe;

    public RecipeStepsFragment getStepsFragment() {
        return stepsFragment;
    }

    private RecipeStepsFragment stepsFragment;
    public PagerAdapter(@NonNull FragmentManager fm, int behavior, Recipe recipe) {
        super(fm, behavior);
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RecipeInfoFragment(recipe);
            case 1:
                return new RecipeIngredientsFragment(recipe);
            case 2:
                stepsFragment = new RecipeStepsFragment(recipe);
                return stepsFragment;
               // return new RecipeStepsFragment(recipe);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Info";
            case 1: return "Składniki";
            case 2: return "Przygotowanie";
        }
        return null;
    }
}
