package com.thicksandwich.minimalistsavingstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
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
    private static final String T2_AMOUNT = "AMOUNT"; //not used
    private static final String T2_TARGETMONTH = "MONTH";

    private static final String T3_TABLENAME = "standing_orders";
    private static final String T3_PK = "ID";
    private static final String T3_EXPENSE = "EXPENSE";
    private static final String T3_AMOUNT = "AMOUNT";
    private static final String T3_REF = "REFERENCE";
    private static final String T3_CAT = "CATEGORY";
    private static final String T3_DATETIME = "DATE_TIME";
    private static final String T3_FREQ = "FREQUENCY";
    private static final String T3_DAY = "RECUR_DAY";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE " + T1_TABLENAME + " (" +
                    T1_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    T1_EXPENSE + " BOOLEAN, " +
                    T1_AMOUNT + " INTEGER, " +
                    T1_REF + " TEXT, " +
                    T1_CAT + " TEXT, " +
                    T1_DATETIME + " TEXT)");
        } catch (Exception e){
            Log.d(TAG, "onCreate: '" + T1_TABLENAME + "' table already exists.");
        }

        try {
            db.execSQL("CREATE TABLE " + T2_TABLENAME + " (" +
                    T2_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    T2_CAT + " TEXT, " +
                    T2_TARGET + " INTEGER, " +
                    T2_AMOUNT + " INTEGER, " +
                    T2_TARGETMONTH + " TEXT)");
        } catch (Exception e){
            Log.d(TAG, "onCreate: '" + T2_TABLENAME + "' table already exists.");
        }

        try {
            db.execSQL("CREATE TABLE " + T3_TABLENAME + " (" +
                    T3_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    T3_EXPENSE + " BOOLEAN, " +
                    T3_AMOUNT + " INTEGER, " +
                    T3_REF + " TEXT, " +
                    T3_CAT + " TEXT, " +
                    T3_DATETIME + " TEXT, " +
                    T3_FREQ + " TEXT, " +
                    T3_DAY + " TEXT)");
        } catch (Exception e){
            Log.d(TAG, "onCreate: '" + T3_TABLENAME + "' table already exists.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    //------------------------------transactions table TABLE 1--------------------------------------
    public boolean addTransaction(boolean expense, int amount, String reference, String category, String date_time) {
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

    public void removeItemT1(long id) {
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

    public Cursor getSummedData(String year, String month) { //sum expenses by unique category (ordered) for StatsFragment
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + T1_CAT + ", SUM(" + T1_AMOUNT + ")" +
                " AS " + T1_AMOUNT + " FROM " +
                T1_TABLENAME + " WHERE " + T1_EXPENSE + " = 1 " + //only expenses
                " AND strftime('%m'," + T1_DATETIME + ")='" + month + "'" + //select month
                " AND strftime('%Y'," + T1_DATETIME + ")='" + year + "'" + //select year
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

    //------------------------------budgeting table TABLE 2-----------------------------------------
    public boolean addBudget(String category, String target, String year, String month, String day){
        SQLiteDatabase db = this.getWritableDatabase();

        String today = year + "-" + month + "-" + day;

        ContentValues contentValues = new ContentValues();
        contentValues.put(T2_CAT, category);
        contentValues.put(T2_TARGET, target);
        contentValues.put(T2_TARGETMONTH, today); //current YYYY-MM-DD

        long result = db.insert(T2_TABLENAME, null, contentValues);
        return result != -1; //return true if result is not -1
    }

    public void getAmountToBudget(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        //copy summed data from current month/year into budgeting table AMOUNT field for selected category

        if(category.equals("Monthly Total")){ //if the category is the monthly total
            Log.d(TAG, "getAmountToBudget: " + category);

            db.execSQL("UPDATE " + T2_TABLENAME +
                    " SET " + T2_AMOUNT + " = (SELECT COALESCE(SUM(" + T1_AMOUNT + "),0) " +
                    " FROM " + T1_TABLENAME +
                    " WHERE " + T1_EXPENSE + " = '1'" +
                    " AND strftime('%Y %m'," + T1_DATETIME + ") = strftime('%Y %m', " + T2_TARGETMONTH + "))" +
                    " WHERE " + T2_CAT + " = 'Monthly Total'");

        } else { //if not monthly total category

            db.execSQL("UPDATE " + T2_TABLENAME +
                    " SET " + T2_AMOUNT + " = (SELECT COALESCE(SUM(" + T1_AMOUNT + "),0) " +
                    " FROM " + T1_TABLENAME +
                    " WHERE " + T1_CAT + " = '" + category + "'" +
                    " AND strftime('%Y %m'," + T1_DATETIME + ") = strftime('%Y %m', " + T2_TARGETMONTH + "))" +
                    " WHERE " + T2_CAT + " = '" + category + "'");

        }
    }

    public boolean budgetExists(String category, String year, String month){
        SQLiteDatabase db = this.getWritableDatabase();

        //select all values in the db that have target month&year = current month and year and
        //category is the same as spinner selection
        Cursor cur = db.rawQuery("SELECT * FROM " + T2_TABLENAME +
                " WHERE " + T2_CAT + " = '" + category + "'" +
                " AND strftime('%Y %m', " + T2_TARGETMONTH + ") = '" + year + " " + month + "'", null);

        if(cur.getCount() <= 0){ //if such a value does not exist
            cur.close();
            return false;
        } else { //if such a value does exist
            cur.close();
            return true;
        }
    }

    public void updateBudget(String category, String target, String year, String month){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + T2_TABLENAME +
                " SET " + T2_TARGET + " = '" + target + "'" +
                " WHERE " + T2_CAT + " = '" + category + "'" +
                " AND strftime('%Y %m', 'now') = '" + year + " " + month + "'");
    }

    //get data to display from the database
    public Cursor getBudgetData(String year, String month) { //YYYY-MM-DD
        SQLiteDatabase db = this.getWritableDatabase();
        //get data in order of date and time
        return db.rawQuery("SELECT *" +
                " FROM " + T2_TABLENAME +
                " WHERE strftime('%Y %m', " + T2_TARGETMONTH + ") = '" + year + " " + month + "'" +
                " ORDER BY " + T2_CAT + " = 'Monthly Total' DESC, " + T2_CAT + " ASC", null);

    }

    //---------------------------fragment_standing_orders table TABLE 3--------------------------------------

    public boolean addStandingOrder(boolean expense, int amount, String reference, String category, String date_time, String frequency, String recurday) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T3_EXPENSE, expense);
        contentValues.put(T3_AMOUNT, amount);
        contentValues.put(T3_REF, reference);
        contentValues.put(T3_CAT, category);
        contentValues.put(T3_DATETIME, date_time);
        contentValues.put(T3_FREQ, frequency);
        contentValues.put(T3_DAY, recurday);

        Log.d("DATABASE_HELPER", "Adding data to " + T3_TABLENAME);

        long result = db.insert(T3_TABLENAME, null, contentValues);

        return result != -1; //return true if result is not -1 (if -1, data couldn't be added)
    }

    public void removeItemT3(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(T3_TABLENAME, T3_PK + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getStandingOrders(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + T3_TABLENAME, null);
    }

    public void updateStandingOrders(Integer id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + T3_TABLENAME +
                " SET " + T3_DATETIME + " = '" + date + "'" +
                " WHERE " + T3_PK + " = '" + id + "'");

    }

}