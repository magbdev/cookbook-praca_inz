package com.example.cookbook.ui.recipes;

import android.content.Intent;
import android.os.Bundle;

import com.example.cookbook.adapter.StringListAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;

import com.example.cookbook.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AddStepsActivity extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static StringListAdapter adapter;
    private ArrayList<String> list;
    private TextInputLayout stepName;
    private Button addButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_steps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = getIntent().getStringArrayListExtra("stepsList");

        recyclerView = findViewById(R.id.stepsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new StringListAdapter(this.getApplicationContext(),list);
        recyclerView.setAdapter(adapter);

        addButton = findViewById(R.id.add_single_step_button);
        stepName = findViewById(R.id.stepNameText);
        saveButton = findViewById(R.id.save_steps_list);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stepName.getEditText().length() != 0){
                    list.add(stepName.getEditText().getText().toString());
                    adapter.notifyDataSetChanged();
                    stepName.getEditText().getText().clear();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("stepsList", list);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}