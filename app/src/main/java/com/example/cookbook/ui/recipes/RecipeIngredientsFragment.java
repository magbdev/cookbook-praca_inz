package com.example.cookbook.ui.recipes;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;

public class RecipeIngredientsFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static IngredientsListAdapter adapter;
    private Recipe recipe;

    public RecipeIngredientsFragment(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        recyclerView = view.findViewById(R.id.recipe_ingredients_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new IngredientsListAdapter(view.getContext(),recipe.getIngredients());
        recyclerView.setAdapter(adapter);
        return view;
    }
}