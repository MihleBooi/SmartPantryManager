
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText edtName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiryDate;
    private int ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        edtName = findViewById(R.id.edtEditIngredientName);
        edtQuantity = findViewById(R.id.edtEditQuantity);
        edtUnit = findViewById(R.id.edtEditUnit);
        edtExpiryDate = findViewById(R.id.edtEditExpiryDate);

        Button btnUpdate = findViewById(R.id.btnUpdateIngredient);

        databaseHelper = new DatabaseHelper(this);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId == -1) {
            Toast.makeText(this,
                    "Ingredient could not be found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadIngredientDetails();

        btnUpdate.setOnClickListener(view -> {
            String name = edtName.getText().toString().trim();
            String quantityText =
                    edtQuantity.getText().toString().trim();
            String unit = edtUnit.getText().toString().trim();
            String expiryDate =
                    edtExpiryDate.getText().toString().trim();

            if (name.isEmpty()
                    || quantityText.isEmpty()
                    || unit.isEmpty()) {
                Toast.makeText(this,
                        "Please fill in the name, quantity and unit",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity;

            try {
                quantity = Double.parseDouble(quantityText);

                if (quantity < 0) {
                    Toast.makeText(this,
                            "Quantity cannot be negative",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Please enter a valid quantity",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int result = databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    expiryDate.isEmpty() ? null : expiryDate
            );

            if (result > 0) {
                Toast.makeText(this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this,
                        "Could not update ingredient",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIngredientDetails() {
        Cursor cursor = databaseHelper.getAllIngredients();
        boolean found = false;

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID));

                if (id == ingredientId) {
                    edtName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_NAME)));

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_QUANTITY));

                    edtQuantity.setText(String.valueOf(quantity));

                    edtUnit.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_UNIT)));

                    String expiryDate = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_EXPIRY));

                    edtExpiryDate.setText(
                            expiryDate == null ? "" : expiryDate);

                    found = true;
                    break;
                }
            }
        } finally {
            cursor.close();
        }

        if (!found) {
            Toast.makeText(this,
                    "Ingredient not found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
