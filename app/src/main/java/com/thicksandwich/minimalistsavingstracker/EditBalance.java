package com.thicksandwich.minimalistsavingstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;
import com.thicksandwich.minimalistsavingstracker.menutabs.Home.HomeFragment;


public class EditBalance extends AppCompatActivity {

    private EditText edit_balancenodp, edit_balancedp1, edit_balancedp2;
    private Button btn_editbalance;
    private TextView txt_currencysymb, txt_decimalpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_edit_layout);

        edit_balancenodp = findViewById(R.id.edit_balancenodp);
        edit_balancedp1 = findViewById(R.id.edit_balancedp1);
        edit_balancedp2 = findViewById(R.id.edit_balancedp2);
        txt_decimalpoint = findViewById(R.id.txt_editbaldecimalpoint);
        btn_editbalance = findViewById(R.id.btn_editbalance);
        txt_currencysymb = findViewById(R.id.txt_balance_yen);

        txt_currencysymb.setText(CurrencyFormat.currency_symbol);

        if(CurrencyFormat.decimal_currency){ //if decimal currency
            edit_balancenodp.setVisibility(View.GONE);
        } else {
            edit_balancedp1.setVisibility(View.GONE);
            edit_balancedp2.setVisibility(View.GONE);
            txt_decimalpoint.setVisibility(View.GONE);
        }

        btn_editbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_amount;

                if(CurrencyFormat.decimal_currency) {
                    if(edit_balancedp2.getText().toString().length()<2){
                        edit_balancedp2.setText("00");
                    }
                    new_amount = edit_balancedp1.getText().toString() + edit_balancedp2.getText().toString();
                } else {
                    new_amount = edit_balancenodp.getText().toString();
                }

                if(new_amount.length() != 0)
                {
                    HomeFragment.BalanceOverride(Integer.parseInt(new_amount)); //override the value of balance
                    HomeFragment.savePrefs();
                    startActivity(new Intent(EditBalance.this, MainActivity.class));
                    finish();

                } else {
                    Snackbar sb_empty = Snackbar.make(findViewById(R.id.balance_edit_layout), "Please insert a value", Snackbar.LENGTH_LONG);
                    sb_empty.show();
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
