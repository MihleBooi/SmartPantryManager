
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddingIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiryDate;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_ingredient);

        edtIngredientName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);

        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        databaseHelper = new DatabaseHelper(this);

        btnSaveIngredient.setOnClickListener(view -> saveIngredient());
    }

    private void saveIngredient() {
        String name = edtIngredientName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String unit = edtUnit.getText().toString().trim();
        String expiryDate = edtExpiryDate.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this,
                    "Please enter the name, quantity and unit.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter a valid quantity.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this,
                    "Quantity must be greater than zero.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (expiryDate.isEmpty()) {
            expiryDate = null;
        }

        long result = databaseHelper.addIngredient(
                name, quantity, unit, expiryDate);

        if (result != -1) {
            Toast.makeText(this,
                    "Ingredient saved successfully! ♡",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this,
                    "Could not save the ingredient.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
