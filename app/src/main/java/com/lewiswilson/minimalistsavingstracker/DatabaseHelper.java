package com.lewiswilson.minimalistsavingstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MST_Income_Expenses.db";
    private static final String TABLE_NAME = "transactions";
    private static final String COL0 = "ID";
    private static final String COL1 = "EXPENSE";
    private static final String COL2 = "AMOUNT";
    private static final String COL3 = "REFERENCE";
    private static final String COL4 = "CATEGORY";
    private static final String COL5 = "DATE_TIME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " BOOLEAN, " +
                COL2 + " INTEGER, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    boolean addData(boolean expense, int amount, String reference, String category, String date_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, expense);
        contentValues.put(COL2, amount);
        contentValues.put(COL3, reference);
        contentValues.put(COL4, category);
        contentValues.put(COL5, date_time);

        Log.d("DATABASE_HELPER", "Adding data to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1; //return true if result is not -1

    }

    public void removeItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL0 + "=?", new String[]{String.valueOf(id)});
    }

    //get all of the data from the database

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
