package com.example.cookbook.ui.recipes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.example.cookbook.R;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.viewmodel.AddRecipeViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddRecipeActivity extends AppCompatActivity {

    private AddRecipeViewModel addRecipesViewModel;
    private Button addIngredientsButton, addStepsButton, save_add_recipe;
    private Recipe recipe;
    private ArrayList<String> stepsList;
    private HashMap<String,String> ingredientsList;
    private TextInputLayout title,description;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_INGREDIENTS = 2;
    private static int RESULT_STEPS = 3;
    FirebaseFirestore db;

    private static final int STORAGE_PERMISSION_CODE = 101;

    private Uri filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_INGREDIENTS && resultCode == RESULT_OK){
            ingredientsList = (HashMap<String, String>)data.getSerializableExtra("ingredientsList");
        }
        else if (requestCode == RESULT_STEPS && resultCode == RESULT_OK){
            stepsList = data.getStringArrayListExtra("stepsList");
        }
        else if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Bitmap bm=null;
            if (data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imageView.setImageBitmap(bm);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addRecipesViewModel =
                ViewModelProviders.of(this).get(AddRecipeViewModel.class);
        setContentView(R.layout.activity_add_recipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(stepsList == null){
            stepsList = new ArrayList<>();
        }
        if (ingredientsList == null){
            ingredientsList = new HashMap<>();
        }

        title = findViewById(R.id.titleText);
        description = findViewById(R.id.descriptionText);

        addIngredientsButton = findViewById(R.id.addIngredientsButton);
        addIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddIngredientsActivity.class);
                intent.putExtra("ingredientsList", ingredientsList);
                startActivityForResult(intent,RESULT_INGREDIENTS);
            }
        });
        addStepsButton = findViewById(R.id.addStepsButton);
        addStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddStepsActivity.class);
                intent.putStringArrayListExtra("stepsList", stepsList);
                startActivityForResult(intent, RESULT_STEPS);
            }
        });

        save_add_recipe = findViewById(R.id.save_add_recipe);
        save_add_recipe.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                recipe = new Recipe();
                recipe.setTitle(title.getEditText().getText().toString());
                recipe.setDescription(description.getEditText().getText().toString());
                recipe.setIngredients(ingredientsList);
                recipe.setSteps(stepsList);
                addRecipesViewModel.sendRecipe(filePath,recipe);
                finish();
            }
        });


        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        STORAGE_PERMISSION_CODE);
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(AddRecipeActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(AddRecipeActivity.this,
                    new String[] { permission },
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(AddRecipeActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}