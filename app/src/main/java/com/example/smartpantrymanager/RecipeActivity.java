
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RecipeActivity extends AppCompatActivity {

    private ListView listRecipes;
    private TextView txtRecipeInstructions;
    private DatabaseHelper databaseHelper;

    private final String[] recipeNames = {

            "Tomato Salad",
            "Cabbage and Carrot Soup",
            "Carrot Salad",
            "Vegetable Mix",
            "Tomato and Cabbage Stir-Fry",
            "Roasted Carrots",
            "Steamed Cabbage",
            "Tomato Soup",
            "Cabbage Salad",
            "Carrot Soup",
            "Tomato and Carrot Salad",
            "Cabbage and Tomato Soup",
            "Mixed Vegetable Soup",
            "Sautéed Carrots and Cabbage",
            "Tomato Vegetable Stew",
            "Carrot and Cabbage Stir-Fry",
            "Fresh Tomato Slices",
            "Steamed Mixed Vegetables",
            "Cabbage and Carrot Mix",
            "Tomato and Carrot Stew"


    };

    private final String[][] requiredIngredients = {
            {"Tomatoes"},
            {"Cabbage", "Carrots"},
            {"Carrots"},
            {"Cabbage", "Carrots", "Tomatoes"},
            {"Tomatoes", "Cabbage"},
            {"Carrots"},
            {"Cabbage"},
            {"Tomatoes"},
            {"Cabbage"},
            {"Carrots"},
            {"Tomatoes", "Carrots"},
            {"Cabbage", "Tomatoes"},
            {"Cabbage", "Carrots"},
            {"Carrots", "Cabbage"},
            {"Tomatoes", "Carrots"},
            {"Carrots", "Cabbage"},
            {"Tomatoes"},
            {"Cabbage", "Carrots"},
            {"Cabbage", "Carrots"},
            {"Tomatoes", "Carrots"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        listRecipes = findViewById(R.id.listRecipes);
        txtRecipeInstructions = findViewById(R.id.txtRecipeInstructions);

        databaseHelper = new DatabaseHelper(this);

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        Set<String> pantryIngredients = new HashSet<>();
        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_NAME));

                if (name != null) {
                    pantryIngredients.add(
                            name.trim().toLowerCase(Locale.ROOT));
                }
            }
        } finally {
            cursor.close();
        }

        ArrayList<String> suggestions = new ArrayList<>();

        for (int i = 0; i < recipeNames.length; i++) {
            boolean hasAllIngredients = true;

            for (String ingredient : requiredIngredients[i]) {
                if (!pantryIngredients.contains(
                        ingredient.toLowerCase(Locale.ROOT))) {
                    hasAllIngredients = false;
                    break;
                }
            }

            if (hasAllIngredients) {
                suggestions.add(recipeNames[i]);
            }
        }

        if (suggestions.isEmpty()) {
            txtRecipeInstructions.setText(
                    "No matching recipes yet. Add more ingredients to your pantry!");
        } else {
            txtRecipeInstructions.setText(
                    "Recipes you can make with your pantry ingredients:");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                suggestions
        );

        listRecipes.setAdapter(adapter);
        listRecipes.setOnItemClickListener((parent, view, position, id) -> {

            String selectedRecipe = suggestions.get(position);

            String ingredients = "";
            String steps = "";

            switch (selectedRecipe) {

                case "Roasted Carrots":
                    ingredients = "Carrots, oil, salt";
                    steps = "1. Wash and slice the carrots.\n\n"
                            + "2. Toss with a little oil and salt.\n\n"
                            + "3. Roast until tender.";
                    break;

                case "Steamed Cabbage":
                    ingredients = "Cabbage, water, salt";
                    steps = "1. Wash and chop the cabbage.\n\n"
                            + "2. Steam or cook with a little water until tender.\n\n"
                            + "3. Add salt and serve.";
                    break;

                case "Tomato Soup":
                    ingredients = "Tomatoes, water, salt";
                    steps = "1. Wash and chop the tomatoes.\n\n"
                            + "2. Simmer with water until soft.\n\n"
                            + "3. Mash or blend carefully and season.";
                    break;

                case "Cabbage Salad":
                    ingredients = "Cabbage, salt, lemon juice (optional)";
                    steps = "1. Wash and finely slice the cabbage.\n\n"
                            + "2. Add salt and optional lemon juice.\n\n"
                            + "3. Toss and serve.";
                    break;

                case "Carrot Soup":
                    ingredients = "Carrots, water, salt";
                    steps = "1. Wash and chop the carrots.\n\n"
                            + "2. Simmer in water until soft.\n\n"
                            + "3. Mash carefully and season.";
                    break;

                case "Tomato and Carrot Salad":
                    ingredients = "Tomatoes, carrots";
                    steps = "1. Wash the tomatoes and carrots.\n\n"
                            + "2. Slice the tomatoes and grate the carrots.\n\n"
                            + "3. Mix and serve.";
                    break;

                case "Cabbage and Tomato Soup":
                    ingredients = "Cabbage, tomatoes, water, salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Simmer with water until tender.\n\n"
                            + "3. Season and serve.";
                    break;

                case "Mixed Vegetable Soup":
                    ingredients = "Cabbage, carrots, water, salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Simmer in water until tender.\n\n"
                            + "3. Season and serve.";
                    break;

                case "Sautéed Carrots and Cabbage":
                    ingredients = "Carrots, cabbage, oil, salt";
                    steps = "1. Wash and slice the vegetables.\n\n"
                            + "2. Heat a little oil in a pan.\n\n"
                            + "3. Cook until tender and season.";
                    break;

                case "Tomato Vegetable Stew":
                    ingredients = "Tomatoes, carrots, water, salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Simmer with a little water until soft.\n\n"
                            + "3. Season and serve.";
                    break;

                case "Carrot and Cabbage Stir-Fry":
                    ingredients = "Carrots, cabbage, oil, salt";
                    steps = "1. Wash and slice the vegetables.\n\n"
                            + "2. Heat a little oil in a pan.\n\n"
                            + "3. Stir-fry until tender and serve.";
                    break;

                case "Fresh Tomato Slices":
                    ingredients = "Tomatoes, salt (optional)";
                    steps = "1. Wash the tomatoes.\n\n"
                            + "2. Slice them carefully.\n\n"
                            + "3. Add optional salt and serve.";
                    break;

                case "Steamed Mixed Vegetables":
                    ingredients = "Cabbage, carrots, water";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Steam or cook with a little water until tender.\n\n"
                            + "3. Serve warm.";
                    break;

                case "Cabbage and Carrot Mix":
                    ingredients = "Cabbage, carrots, salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Steam or boil until tender.\n\n"
                            + "3. Season and serve.";
                    break;

                case "Tomato and Carrot Stew":
                    ingredients = "Tomatoes, carrots, water, salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Simmer together until tender.\n\n"
                            + "3. Season and serve.";
                    break;

                case "Tomato Salad":
                    ingredients = "• Tomatoes\n• Salt\n• Olive oil";
                    steps = "1. Wash and slice the tomatoes.\n\n"
                            + "2. Add salt and olive oil.\n\n"
                            + "3. Mix gently and serve.";
                    break;

                case "Cabbage and Carrot Soup":
                    ingredients = "• Cabbage\n• Carrots\n• Water\n• Salt";
                    steps = "1. Wash and chop the cabbage and carrots.\n\n"
                            + "2. Add them to a pot with water.\n\n"
                            + "3. Simmer until the vegetables are soft.\n\n"
                            + "4. Add salt and serve.";
                    break;

                case "Carrot Salad":
                    ingredients = "• Carrots\n• Salt\n• Lemon juice (optional)";
                    steps = "1. Wash and grate the carrots.\n\n"
                            + "2. Add a little salt and lemon juice if available.\n\n"
                            + "3. Mix and serve.";
                    break;

                case "Vegetable Mix":
                    ingredients = "• Cabbage\n• Carrots\n• Tomatoes\n• Salt";
                    steps = "1. Wash and chop all vegetables.\n\n"
                            + "2. Cook them together in a pan or pot until tender.\n\n"
                            + "3. Add salt and serve.";
                    break;

                case "Tomato and Cabbage Stir-Fry":
                    ingredients = "• Tomatoes\n• Cabbage\n• Oil\n• Salt";
                    steps = "1. Wash and chop the vegetables.\n\n"
                            + "2. Heat a little oil in a pan.\n\n"
                            + "3. Add the cabbage and cook until slightly soft.\n\n"
                            + "4. Add tomatoes and salt, cook briefly, and serve.";
                    break;
            }

            Intent intent = new Intent(
                    RecipeActivity.this,
                    RecipeDetailsActivity.class
            );

            intent.putExtra("recipeName", selectedRecipe);
            intent.putExtra("ingredients", ingredients);
            intent.putExtra("steps", steps);

            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
