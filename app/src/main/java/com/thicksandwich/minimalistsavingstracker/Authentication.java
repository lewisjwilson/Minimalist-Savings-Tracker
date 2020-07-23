package com.thicksandwich.minimalistsavingstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import static android.content.ContentValues.TAG;

public class Authentication extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    public static String current_pin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_screen);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        current_pin = sharedPreferences.getString(CURRENT_PIN, "0000"); //default pin is 0000

        final TextView currentpin = findViewById(R.id.current_pin);
        currentpin.setText(current_pin);
        final EditText edit_pin = findViewById(R.id.edit_pin);
        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String login_try = edit_pin.getText().toString();

                if(login_try.equals(current_pin)){
                    startActivity(new Intent(Authentication.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar sb_pin_error = Snackbar.make(findViewById(android.R.id.content),
                            "Incorrect Pin", Snackbar.LENGTH_LONG);
                    sb_pin_error.show();
                }
            }
        });
    }
}
