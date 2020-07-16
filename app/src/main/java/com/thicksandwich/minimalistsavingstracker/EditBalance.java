package com.thicksandwich.minimalistsavingstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.ui.Home.HomeFragment;


public class EditBalance extends AppCompatActivity {

    private EditText et_edit_balance;
    private Button btn_editbalance;
    private TextView txt_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_edit_layout);

        et_edit_balance = findViewById(R.id.et_edit_balance);
        btn_editbalance = findViewById(R.id.btn_editbalance);
        txt_reset = findViewById(R.id.txt_reset);

        btn_editbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_edit_balance.length() != 0)
                {
                    HomeFragment.BalanceOverride(Integer.parseInt(et_edit_balance.getText().toString())); //override the value of balance
                    HomeFragment.savePrefs();
                    startActivity(new Intent(EditBalance.this, MainActivity.class));
                    finish();

                } else {
                    Snackbar sb_empty = Snackbar.make(findViewById(R.id.balance_edit_layout), "Please insert a value", Snackbar.LENGTH_LONG);
                    sb_empty.show();
                }
            }
        });

        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment.balance_override = 0;
                HomeFragment.difference = 0;
                HomeFragment.savePrefs();
                startActivity(new Intent(EditBalance.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
