package com.example.cookbook.ui.recipes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.model.User;
import com.example.cookbook.viewmodel.RecipeDetailsViewModel;


public class RecipeInfoFragment extends Fragment {

    private Recipe recipe;
    private RecipeDetailsViewModel viewModel;
    private TextView title,description,author;
    private ImageView image;

    public RecipeInfoFragment(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recipe_info, container, false);
        viewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
        title = view.findViewById(R.id.recipe_info_title);
        title.setText(recipe.getTitle());

        description = view.findViewById(R.id.recipe_info_description);
        description.setText(recipe.getDescription());

        image = view.findViewById(R.id.recipe_info_image);
        Glide.with(image.getContext()).load(recipe.getImageUrl()).into(image);

        author = view.findViewById(R.id.recipe_info_author);
        viewModel.fetchData(recipe.getUserID()).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                author.setText("Autor "+user.getFirstName()+" "+user.getLastName());
            }
        });

        return view;
    }
}