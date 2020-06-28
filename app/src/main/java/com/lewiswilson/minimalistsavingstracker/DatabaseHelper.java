package com.lewiswilson.minimalistsavingstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

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

    //get data to display from the database
    public Cursor getDisplayData(String year , String month) { //YYYY; MM
        SQLiteDatabase db = this.getWritableDatabase();
        //get data in order of date and time
        return db.rawQuery("SELECT *" +
                        " FROM " + TABLE_NAME +
                        " WHERE strftime('%m'," + COL5 + ")='" + month + "'" + //select month
                        " AND strftime('%Y'," + COL5 + ")='" + year + "'" + //select year
                        " ORDER BY datetime(" + COL5 + ") DESC", null);
    }

    //get all data from the database
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //get data in order of date and time
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    //sum all transactions
    public int sumTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor expenCur =  db.rawQuery("SELECT SUM(" + COL2 + ") AS expenses" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL1 + "='1'", null);

        int expenses = 0;
        if (expenCur.moveToFirst()) {
            expenses = expenCur.getInt(expenCur.getColumnIndex("expenses"));
        }

        Cursor incomeCur = db.rawQuery("SELECT SUM(" + COL2 + ") AS income" +
                " FROM " + TABLE_NAME +
                " WHERE " + COL1 + "='0'", null);

        int income = 0;
        if (incomeCur.moveToFirst()) {
            income = incomeCur.getInt(incomeCur.getColumnIndex("income"));
        }

        return income - expenses;
    }
}
