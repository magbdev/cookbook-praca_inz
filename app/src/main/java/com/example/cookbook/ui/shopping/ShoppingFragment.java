package com.example.cookbook.ui.shopping;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookbook.R;
import com.example.cookbook.adapter.ShoppingListAdapter;
import com.example.cookbook.viewmodel.ShoppingViewModel;

import java.util.ArrayList;

public class ShoppingFragment extends Fragment implements ShoppingListAdapter.OnDeleteClickListener {

    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter adapter;
    private static RecyclerView recyclerView;

    public static ShoppingFragment newInstance() {
        return new ShoppingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        View root = inflater.inflate(R.layout.shopping_fragment, container, false);
        recyclerView = root.findViewById(R.id.shoppingListFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new ShoppingListAdapter(this);
        recyclerView.setAdapter(adapter);

        mViewModel.fetchRecipe().observe(getViewLifecycleOwner(), strings -> {
            adapter.addShoppingList((ArrayList<String>) strings);
            adapter.notifyDataSetChanged();
        });
        return root;
    }

    @Override
    public void deleteItem(int position, ArrayList<String> list) {
        mViewModel.updateShoppingList(adapter.getList());
    }
}