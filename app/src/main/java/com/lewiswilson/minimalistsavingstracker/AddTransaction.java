package com.lewiswilson.minimalistsavingstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;


public class AddTransaction extends AppCompatActivity {

    DatabaseHelper myDB;
    EditText edit_amount, edit_reference;
    Button btn_submit;
    String new_amount, new_reference;
    int new_amount_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        myDB = new DatabaseHelper(this);

        edit_amount = findViewById(R.id.edit_amount);
        edit_reference = findViewById(R.id.edit_reference);
        btn_submit = findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new_amount = edit_amount.getText().toString();
                new_reference = edit_reference.getText().toString();
                if((new_amount.length() != 0)&&(new_reference.length() != 0))
                {
                    new_amount_val = Integer.parseInt(new_amount); //parse string value as integer
                    AddData(new_amount_val, new_reference);
                    edit_amount.setText("");
                    edit_reference.setText("");

                    startActivity(new Intent(AddTransaction.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                } else {
                    Snackbar sb_empty = Snackbar.make(findViewById(R.id.add_transactions), "One of more fields are empty", Snackbar.LENGTH_LONG);
                    sb_empty.show();
                }
            }
        });

    }

    private void AddData(int amount, String reference) {
        boolean insertData = myDB.addData(amount, reference);

        if(!insertData) {
            Snackbar sb_insert_error = Snackbar.make(findViewById(R.id.add_transactions), "Error inserting data", Snackbar.LENGTH_LONG);
            sb_insert_error.show();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
