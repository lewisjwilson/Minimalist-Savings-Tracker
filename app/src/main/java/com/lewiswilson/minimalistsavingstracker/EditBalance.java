package com.lewiswilson.minimalistsavingstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.lewiswilson.minimalistsavingstracker.ui.home.HomeFragment;


public class EditBalance extends AppCompatActivity {

    private EditText et_edit_balance;
    private Button btn_editbalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_edit_layout);

        et_edit_balance = findViewById(R.id.et_edit_balance);
        btn_editbalance = findViewById(R.id.btn_editbalance);

        btn_editbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_edit_balance.length() != 0)
                {
                    HomeFragment.balance_override = Integer.parseInt(et_edit_balance.getText().toString());
                    HomeFragment.refresh = true;
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
