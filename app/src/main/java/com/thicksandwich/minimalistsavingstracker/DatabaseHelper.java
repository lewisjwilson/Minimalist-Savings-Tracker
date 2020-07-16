package com.thicksandwich.minimalistsavingstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MST_Income_Expenses.db";

    private static final String T1_TABLENAME = "transactions";
    private static final String T1_PK = "ID";
    private static final String T1_EXPENSE = "EXPENSE";
    private static final String T1_AMOUNT = "AMOUNT";
    private static final String T1_REF = "REFERENCE";
    private static final String T1_CAT = "CATEGORY";
    private static final String T1_DATETIME = "DATE_TIME";

    private static final String T2_TABLENAME = "budgeting";
    private static final String T2_PK = "ID";
    private static final String T2_CAT = "CATEGORY";
    private static final String T2_TARGET = "TARGET";
    private static final String T2_AMOUNT = "AMOUNT";
    private static final String T2_TARGETMONTH = "MONTH";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + T1_TABLENAME + " (" +
                T1_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                T1_EXPENSE + " BOOLEAN, " +
                T1_AMOUNT + " INTEGER, " +
                T1_REF + " TEXT, " +
                T1_CAT + " TEXT, " +
                T1_DATETIME + " TEXT)");

        db.execSQL("CREATE TABLE " + T2_TABLENAME + " (" +
                T2_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                T2_CAT + " TEXT, " +
                T2_TARGET + " INTEGER, " +
                T2_AMOUNT + " INTEGER, " +
                T2_TARGETMONTH + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String t1query = "DROP TABLE IF EXISTS " + T1_TABLENAME;
        String t2query = "DROP TABLE IF EXISTS " + T2_TABLENAME;
        db.execSQL(t1query);
        db.execSQL(t2query);
        onCreate(db);
    }

    boolean addBudget(boolean category, int target){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T2_CAT, target);
        contentValues.put(T2_TARGET, target);

        long result = db.insert(T2_TABLENAME, null, contentValues);

        return result != -1; //return true if result is not -1

    }

    boolean addTransaction(boolean expense, int amount, String reference, String category, String date_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T1_EXPENSE, expense);
        contentValues.put(T1_AMOUNT, amount);
        contentValues.put(T1_REF, reference);
        contentValues.put(T1_CAT, category);
        contentValues.put(T1_DATETIME, date_time);

        Log.d("DATABASE_HELPER", "Adding data to " + T1_TABLENAME);

        long result = db.insert(T1_TABLENAME, null, contentValues);

        return result != -1; //return true if result is not -1

    }

    public void removeItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(T1_TABLENAME, T1_PK + "=?", new String[]{String.valueOf(id)});
    }

    //get data to display from the database
    public Cursor getDisplayData(String year , String month) { //YYYY; MM
        SQLiteDatabase db = this.getWritableDatabase();
        //get data in order of date and time
        return db.rawQuery("SELECT *" +
                        " FROM " + T1_TABLENAME +
                        " WHERE strftime('%m'," + T1_DATETIME + ")='" + month + "'" + //select month
                        " AND strftime('%Y'," + T1_DATETIME + ")='" + year + "'" + //select year
                        " ORDER BY datetime(" + T1_DATETIME + ") DESC", null);
    }

    //get all data from the database
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //get data in order of date and time
        return db.rawQuery("SELECT * FROM " + T1_TABLENAME, null);
    }

    public Cursor getSummedData() { //sum expenses by unique category (ordered)
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + T1_CAT + ", SUM(" + T1_AMOUNT + ")" +
                " AS " + T1_AMOUNT + " FROM " +
                T1_TABLENAME + " WHERE " + T1_EXPENSE + " = 1 " +
                "GROUP BY " + T1_CAT +
                " ORDER BY " + T1_AMOUNT + " ASC", null);
    }

    //sum all transactions
    public int sumTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor expenCur =  db.rawQuery("SELECT SUM(" + T1_AMOUNT + ") AS expenses" +
                " FROM " + T1_TABLENAME +
                " WHERE " + T1_EXPENSE + "='1'", null);

        int expenses = 0;
        if (expenCur.moveToFirst()) {
            expenses = expenCur.getInt(expenCur.getColumnIndex("expenses"));
        }

        Cursor incomeCur = db.rawQuery("SELECT SUM(" + T1_AMOUNT + ") AS income" +
                " FROM " + T1_TABLENAME +
                " WHERE " + T1_EXPENSE + "='0'", null);

        int income = 0;
        if (incomeCur.moveToFirst()) {
            income = incomeCur.getInt(incomeCur.getColumnIndex("income"));
        }

        expenCur.close();
        incomeCur.close();
        return income - expenses;
    }
}
