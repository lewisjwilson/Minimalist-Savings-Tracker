package com.thicksandwich.minimalistsavingstracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;


public class AddTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseHelper myDB;
    private ToggleButton btn_income_expenses; //not checked = income, checked = expense
    private TextView txt_minus;
    private EditText edit_amount, edit_reference, edit_date;
    private String new_amount, new_reference, new_category, new_date;
    private Spinner edit_category;
    private Button btn_submit;
    private int new_amount_val;

    public static ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        myDB = new DatabaseHelper(this);

        btn_income_expenses = findViewById(R.id.btn_income_expenses);
        txt_minus = findViewById(R.id.txt_minus_rv);
        edit_amount = findViewById(R.id.edit_amount);
        edit_reference = findViewById(R.id.edit_reference);
        edit_category = findViewById(R.id.spn_cat_add);
        edit_date = findViewById(R.id.edit_date);
        btn_submit = findViewById(R.id.btn_submit);

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
        edit_category.setAdapter(adapter);
        edit_category.setOnItemSelectedListener(this);


        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new_amount = edit_amount.getText().toString();
                new_reference = edit_reference.getText().toString();
                new_category = edit_category.getSelectedItem().toString();
                new_date = edit_date.getText().toString();

                if((new_amount.length() != 0)&&(new_reference.length() != 0)&&(new_category.length() != 0)&&(new_date.length() != 0))
                {
                    new_amount_val = Integer.parseInt(new_amount); //parse string value as integer
                    AddData(new_amount_val, new_reference, new_category, new_date);
                    edit_amount.setText("");
                    edit_reference.setText("");
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

    private void AddData(int amount, String reference, String category, String date_time) {

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
        edit_category.setAdapter(adapter);
        edit_category.setOnItemSelectedListener(this);

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
}
