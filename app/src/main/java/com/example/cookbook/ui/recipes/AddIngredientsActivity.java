package com.example.cookbook.ui.recipes;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.example.cookbook.R;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;

public class AddIngredientsActivity extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static IngredientsListAdapter adapter;
    private HashMap<String,String> list;
    private TextInputLayout ingredientName,ingredientQuantity;
    private Button addButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = (HashMap<String,String>)getIntent().getSerializableExtra("ingredientsList");

        recyclerView = findViewById(R.id.ingredientsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new IngredientsListAdapter(this.getApplicationContext(),list);
        recyclerView.setAdapter(adapter);

        addButton = findViewById(R.id.add_single_ingredient_button);
        ingredientName = findViewById(R.id.ingredientNameText);
        ingredientQuantity = findViewById(R.id.ingredientQuantityText);
        saveButton = findViewById(R.id.save_ingredients_list);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ingredientName.getEditText().getText().length() != 0 && ingredientQuantity.getEditText().getText().length() != 0){
                    list.put(ingredientName.getEditText().getText().toString(),ingredientQuantity.getEditText().getText().toString());
                    adapter.notifyDataSetChanged();
                    ingredientName.getEditText().getText().clear();
                    ingredientQuantity.getEditText().getText().clear();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ingredientsList", list);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}