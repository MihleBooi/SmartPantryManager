
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listPantry;
    private TextView txtEmptyPantry;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ingredientList;
    private ArrayList<Integer> ingredientIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this, RecipeActivity.class);
            startActivity(intent);
        });


        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        databaseHelper = new DatabaseHelper(this);
        ingredientList = new ArrayList<>();
        ingredientIds = new ArrayList<>();


        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                ingredientList
        ) {
            @Override
            public View getView(int position, View convertView,
                                android.view.ViewGroup parent) {

                View row = super.getView(position, convertView, parent);

                TextView textView = row.findViewById(android.R.id.text1);
                String item = getItem(position);

                if (item != null && item.contains("⚠ EXPIRED")) {
                    textView.setTextColor(
                            android.graphics.Color.parseColor("#D32F2F"));
                } else if (item != null &&
                        (item.contains("⚠ EXPIRES TODAY")
                                || item.contains("⚠ EXPIRING SOON"))) {
                    textView.setTextColor(
                            android.graphics.Color.parseColor("#EF6C00"));
                } else if (item != null && item.contains("Expires:")) {
                    textView.setTextColor(
                            android.graphics.Color.parseColor("#2E7D32"));
                } else {
                    textView.setTextColor(
                            android.graphics.Color.DKGRAY);
                }

                return row;
            }
        };

        listPantry.setAdapter(adapter);

        listPantry.setOnItemClickListener((parent, view, position, id) -> {
            int ingredientId = ingredientIds.get(position);

            Intent intent = new Intent(
                    PantryActivity.this,
                    EditIngredientActivity.class
            );

            intent.putExtra("ingredient_id", ingredientId);

            startActivity(intent);
        });
        listPantry.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(PantryActivity.this)
                    .setTitle("Delete ingredient")
                    .setMessage("Are you sure you want to delete "
                            + ingredientList.get(position) + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int ingredientId = ingredientIds.get(position);

                        databaseHelper.deleteIngredient(ingredientId);

                        Toast.makeText(
                                PantryActivity.this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadIngredients();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddingIngredientActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredientList.clear();
        ingredientIds.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID));

                ingredientIds.add(id);

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_UNIT));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_EXPIRY));

                String item = name + " — " + quantity + " " + unit;

                if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                    item += "\nExpires: " + expiryDate;

                    try {
                        java.text.SimpleDateFormat dateFormat =
                                new java.text.SimpleDateFormat("yyyy-MM-dd",
                                        java.util.Locale.getDefault());

                        dateFormat.setLenient(false);

                        java.util.Date expiry =
                                dateFormat.parse(expiryDate.trim());

                        java.util.Date today = new java.util.Date();

                        java.text.SimpleDateFormat dayFormat =
                                new java.text.SimpleDateFormat("yyyyMMdd",
                                        java.util.Locale.getDefault());

                        long expiryDay = Long.parseLong(dayFormat.format(expiry));
                        long todayDay = Long.parseLong(dayFormat.format(today));

                        if (expiryDay < todayDay) {
                            item += "\n⚠ EXPIRED";
                        } else if (expiryDay == todayDay) {
                            item += "\n⚠ EXPIRES TODAY";
                        } else if (expiryDay - todayDay <= 3) {
                            item += "\n⚠ EXPIRING SOON";
                        }

                    } catch (java.text.ParseException e) {
                        item += "\nCheck expiry date format";
                    }
                }

                ingredientList.add(item);
            }
        } finally {
            cursor.close();
        }

        adapter.notifyDataSetChanged();

        if (ingredientList.isEmpty()) {
            txtEmptyPantry.setVisibility(View.VISIBLE);
            listPantry.setVisibility(View.GONE);
        } else {
            txtEmptyPantry.setVisibility(View.GONE);
            listPantry.setVisibility(View.VISIBLE);
        }
    }
}
