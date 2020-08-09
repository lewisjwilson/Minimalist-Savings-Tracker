package com.thicksandwich.minimalistsavingstracker.initialization;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.R;

import java.util.ArrayList;

public class PinSetup extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "";
    private EditText pin, verif;
    private ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_setup);

        pin = findViewById(R.id.edit_pinsetuppin);
        verif = findViewById(R.id.edit_pinsetupverif);
        Button next = findViewById(R.id.btn_pinsetupnext);
        final Spinner spn_currencies = findViewById(R.id.spn_currencies);

        ArrayList<String> currency_list = new ArrayList<>();
        currency_list.add("British Pound (£)");
        currency_list.add("Japanese Yen (¥)");
        currency_list.add("US Dollar ($)");

        //fill spinner with arraydata from strings
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currency_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_currencies.setAdapter(adapter);
        spn_currencies.setOnItemSelectedListener(this);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin_str = pin.getText().toString();
                String verif_str = verif.getText().toString();
                Log.d(TAG, "PinSetup: " + pin_str + " " + verif_str);

                if(pin_str.length()>=4) {
                    if (pin_str.equals(verif_str)) {

                        String currency_choice = spn_currencies.getSelectedItem().toString();
                        String currency_symbol = currency_choice.substring(currency_choice.indexOf("(")+1,currency_choice.indexOf(")"));

                        //start new activity --> setup security question
                        Intent intent = new Intent(PinSetup.this, TwoFactorSetup.class);
                        intent.putExtra("pin", pin_str);
                        intent.putExtra("currency", currency_symbol);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar sb_pin_error = Snackbar.make(findViewById(android.R.id.content),
                                "New pins do not match", Snackbar.LENGTH_LONG);
                        sb_pin_error.show();
                    }
                } else {
                    Snackbar sb_pin_short = Snackbar.make(findViewById(android.R.id.content),
                            "Pin must be 4 characters or more", Snackbar.LENGTH_LONG);
                    sb_pin_short.show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
