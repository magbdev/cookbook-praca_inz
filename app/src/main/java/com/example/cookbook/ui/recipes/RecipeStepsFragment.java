package com.example.cookbook.ui.recipes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookbook.R;
import com.example.cookbook.adapter.StepsCardsAdapter;
import com.example.cookbook.model.Recipe;


public class RecipeStepsFragment extends Fragment {

    private Recipe recipe;
    private StepsCardsAdapter adapter;
    private ViewPager viewPager;


    public RecipeStepsFragment(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        viewPager = view.findViewById(R.id.viewPagerSteps);
        adapter = new StepsCardsAdapter(recipe.getSteps(),this.getContext());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setStepItem(position);
                ((RecipeDetailsActivity)getActivity()).setStepNumber(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    public void setStepItem(int item) {
        viewPager.setCurrentItem(item);
    }
}