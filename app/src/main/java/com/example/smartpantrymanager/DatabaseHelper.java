
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    public long addIngredient(String name, double quantity,
                              String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiryDate);

        return db.insert(TABLE_PANTRY, null, values);
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY, null, null, null,
                null, null, COL_NAME + " ASC"
        );
    }

    public int updateIngredient(int id, String name,
                                double quantity, String unit,
                                String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        values.put(COL_EXPIRY, expiryDate);

        return db.update(
                TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY, COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }
}
