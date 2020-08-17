package com.thicksandwich.minimalistsavingstracker.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.MainActivity;
import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.forgotpin.ForgotPin;
import com.thicksandwich.minimalistsavingstracker.initialization.PinSetup;


public class Authentication extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    public static final String SEC_QUESTION = "security_question";
    public static final String SEC_ANSWER = "security_answer";
    public static String current_pin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_screen);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //if sharepreferences does not have a pin, security question or security answer, initialise PinSetup.java
        if(!sharedPreferences.contains(CURRENT_PIN)||!sharedPreferences.contains(SEC_QUESTION)||!sharedPreferences.contains(SEC_ANSWER)){
            startActivity(new Intent(Authentication.this, PinSetup.class));
        }
        current_pin = sharedPreferences.getString(CURRENT_PIN, "0000"); //default pin is 0000

        final EditText edit_pin = findViewById(R.id.edit_twofactor_pin);
        Button btn_login = findViewById(R.id.btn_login);
        TextView forgot_pin = findViewById(R.id.txt_forgotpin);

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

        forgot_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Authentication.this, ForgotPin.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
