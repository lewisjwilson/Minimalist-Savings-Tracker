package com.thicksandwich.minimalistsavingstracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

import static android.content.ContentValues.TAG;


public class AddTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FIRST_TIME_TRANS = "first_time_trans";
    private SharedPreferences sharedPreferences;

    private DatabaseHelper myDB;
    private ToggleButton btn_income_expenses; //not checked = income, checked = expense
    private TextView txt_minus, txt_currencysymbol, txt_decimalpoint;
    private EditText edit_amountnodp, edit_amountdp1, edit_amountdp2, edit_ref, edit_date;
    private String new_amount, new_reference, new_category, new_date;
    private Spinner spn_cat;
    private Button btn_now, btn_submit;
    private CheckBox cb_standingorder;
    private ImageButton btn_hints;
    private int new_amount_val;

    public static ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        myDB = new DatabaseHelper(this);

        btn_hints = findViewById(R.id.btn_hints);
        btn_income_expenses = findViewById(R.id.btn_income_expenses);
        txt_minus = findViewById(R.id.txt_minus_rv);
        txt_currencysymbol = findViewById(R.id.txt_currencysymbol);
        txt_decimalpoint = findViewById(R.id.txt_decimalpoint);
        edit_amountnodp = findViewById(R.id.edit_amountnodp);
        edit_amountdp1 = findViewById(R.id.edit_amountdp1);
        edit_amountdp2 = findViewById(R.id.edit_amountdp2);
        edit_ref = findViewById(R.id.edit_ref);
        spn_cat = findViewById(R.id.spn_cat_add);
        edit_date = findViewById(R.id.edit_date);
        btn_now = findViewById(R.id.btn_now);
        cb_standingorder = findViewById(R.id.cb_standingorder);
        btn_submit = findViewById(R.id.btn_submit);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //check for first time run
        if(sharedPreferences.getBoolean(FIRST_TIME_TRANS, true)){ //if first time
            Log.d(TAG, "AddTransaction: First time run");
            firstTimeHints(this, btn_income_expenses, edit_amountnodp, edit_ref, spn_cat, edit_date, btn_submit);
            sharedPreferences.edit().putBoolean(FIRST_TIME_TRANS, false).apply();
        }

        txt_currencysymbol.setText(CurrencyFormat.currency_symbol);

        if(CurrencyFormat.decimal_currency){
                edit_amountnodp.setVisibility(View.INVISIBLE);
        } else {
                edit_amountdp1.setVisibility(View.GONE);
                edit_amountdp2.setVisibility(View.GONE);
                txt_decimalpoint.setVisibility(View.GONE);
        }

        btn_hints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstTimeHints(AddTransaction.this, btn_income_expenses, edit_amountnodp, edit_ref, spn_cat, edit_date, btn_submit);
            }
        });

        btn_income_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_income_expenses.isChecked()){ //expense
                    txt_minus.setVisibility(View.VISIBLE);
                    btn_income_expenses.setBackgroundColor(Color.parseColor("#3EB82504"));
                    ChangeSpinnerContent(btn_income_expenses.isChecked());
                } else { //income
                    txt_minus.setVisibility(View.INVISIBLE);
                    btn_income_expenses.setBackgroundColor(Color.parseColor("#3E0AC800"));
                    ChangeSpinnerContent(btn_income_expenses.isChecked());
                }
            }
        });

        //fill spinner with arraydata from strings
        adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_cat.setAdapter(adapter);
        spn_cat.setOnItemSelectedListener(this);

        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateNow();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "";

            @Override
            public void onClick(View v) {

                if(CurrencyFormat.decimal_currency) {
                    if(edit_amountdp2.getText().toString().length()<2){
                        edit_amountdp2.setText("00");
                    }
                    new_amount = edit_amountdp1.getText().toString() + edit_amountdp2.getText().toString();
                } else {
                    new_amount = edit_amountnodp.getText().toString();
                }

                new_reference = edit_ref.getText().toString();
                new_category = spn_cat.getSelectedItem().toString();
                new_date = edit_date.getText().toString();

                if((new_amount.length() != 0)&&(new_reference.length() != 0)&&(new_category.length() != 0)&&(new_date.length() != 0))
                {
                    new_amount_val = Integer.parseInt(new_amount); //parse string value as integer

                    if(cb_standingorder.isChecked()){ //if item is a standing order
                        AddStandingOrder(new_amount_val, new_reference, new_category, new_date, "monthly"); //only "monthly" is supported right now
                    }

                    AddData(new_amount_val, new_reference, new_category, new_date);
                    edit_amountnodp.setText("");
                    edit_ref.setText("");
                    edit_date.setText("");

                    startActivity(new Intent(AddTransaction.this, MainActivity.class));
                    finish();

                } else {
                    Snackbar sb_empty = Snackbar.make(findViewById(R.id.add_transactions), "One of more fields are empty", Snackbar.LENGTH_LONG);
                    sb_empty.show();
                }
            }
        });

    }

    private void AddStandingOrder(int amount, String reference, String category, String date_time, String frequency) {

        //checking if item is an expense or income
        boolean bool_expense = false;
        if(btn_income_expenses.isChecked()){
            bool_expense = true;
        }

        boolean insertStOrder= myDB.addStandingOrder(bool_expense, amount, reference, category, date_time, frequency);

        if(!insertStOrder) { //if data could not be added
            Snackbar sb_storder_error = Snackbar.make(findViewById(R.id.add_transactions), "Error inserting Standing Order data", Snackbar.LENGTH_LONG);
            sb_storder_error.show();
        }

    }

    private void AddData(int amount, String reference, String category, String date_time) {

        //checking if item is an expense or income
        boolean bool_expense = false;
        if(btn_income_expenses.isChecked()){
            bool_expense = true;
        }
        boolean insertData = myDB.addTransaction(bool_expense, amount, reference, category, date_time);

        if(!insertData) {
            Snackbar sb_insert_error = Snackbar.make(findViewById(R.id.add_transactions), "Error inserting data", Snackbar.LENGTH_LONG);
            sb_insert_error.show();
        }

    }

    private void dateNow() {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        edit_date.setText(currentDateandTime);
    }

    private void datePicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        final int mYear = mcurrentDate.get(Calendar.YEAR);
        final int mMonth = mcurrentDate.get(Calendar.MONTH);
        final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(AddTransaction.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                timePicker(selectedyear, selectedmonth, selectedday);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    private void timePicker(final int year, final int month, final int day){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                        int mHour = hourOfDay;

                        String selected_month_day = String.format("%02d-%02d", month, day);
                        String selected_time = String.format("%02d:%02d", mHour, minute);
                        edit_date.setText("" + year + "-" + selected_month_day + " " + selected_time);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public void ChangeSpinnerContent(boolean expense) {
        if(expense==true){
            //fill spinner with arraydata from strings
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.expense_categories, android.R.layout.simple_spinner_item);
        } else {
            //fill spinner with arraydata from strings
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.income_categories, android.R.layout.simple_spinner_item);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_cat.setAdapter(adapter);
        spn_cat.setOnItemSelectedListener(this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void firstTimeHints(Activity activity, Button expense_income, EditText amount, EditText reference, Spinner categories, EditText datetime, Button submitbutton) {

        final FancyShowCaseView intro = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Let's add our first transaction.")
                .titleStyle(R.style.HintsStyle, Gravity.CENTER)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView income_expense = new FancyShowCaseView.Builder(activity)
                .focusOn(expense_income)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Click here to toggle between Expense/Income")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView mAmount = new FancyShowCaseView.Builder(activity)
                .focusOn(amount)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Insert the amount of the transaction here.")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView ref = new FancyShowCaseView.Builder(activity)
                .focusOn(reference)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Include a reference.")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView category = new FancyShowCaseView.Builder(activity)
                .focusOn(categories)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Choose a category.")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView date = new FancyShowCaseView.Builder(activity)
                .focusOn(datetime)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Include the date from the receipt etc.")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView submit = new FancyShowCaseView.Builder(activity)
                .focusOn(submitbutton)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .backgroundColor(ContextCompat.getColor(this, R.color.colorHintBg))
                .title("Finally, submit your transaction, and that's it!")
                .titleStyle(R.style.HintsStyle, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .enableAutoTextPosition()
                .build();

        FancyShowCaseQueue mQueue = new FancyShowCaseQueue()
                .add(intro)
                .add(income_expense)
                .add(mAmount)
                .add(ref)
                .add(category)
                .add(date)
                .add(submit);

        mQueue.show();
    }
}
