
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        TextView txtRecipeName = findViewById(R.id.txtRecipeName);
        TextView txtRecipeIngredients = findViewById(R.id.txtRecipeIngredients);
        TextView txtRecipeSteps = findViewById(R.id.txtRecipeSteps);

        String recipeName = getIntent().getStringExtra("recipeName");
        String ingredients = getIntent().getStringExtra("ingredients");
        String steps = getIntent().getStringExtra("steps");

        txtRecipeName.setText(recipeName);
        txtRecipeIngredients.setText(ingredients);
        txtRecipeSteps.setText(steps);
    }
}
