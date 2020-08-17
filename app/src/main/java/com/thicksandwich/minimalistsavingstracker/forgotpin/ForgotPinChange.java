package com.thicksandwich.minimalistsavingstracker.forgotpin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.MyApplication;
import com.thicksandwich.minimalistsavingstracker.login.Authentication;
import com.thicksandwich.minimalistsavingstracker.R;

public class ForgotPinChange extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    private SharedPreferences sharedPreferences;

    EditText new_pin, verif_pin;
    Button change_pin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pin_change);

        new_pin = findViewById(R.id.edit_newanswer);
        verif_pin = findViewById(R.id.edit_newanswerverif);
        change_pin = findViewById(R.id.btn_forgotpinchange);

        change_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_pin_str = new_pin.getText().toString();
                String verif_pin_str = verif_pin.getText().toString();

                if (new_pin_str.length() >= 4) {
                    if (new_pin_str.equals(verif_pin_str)) {
                        savePrefs(new_pin_str);
                        Intent intent = new Intent(ForgotPinChange.this, Authentication.class);
                        ForgotPinChange.this.startActivity(intent);
                    } else {
                        Snackbar sb_newpin_error = Snackbar.make(ForgotPinChange.this.findViewById(android.R.id.content),
                                "New pins do not match", Snackbar.LENGTH_LONG);
                        sb_newpin_error.show();
                    }
                } else {
                    Snackbar sb_pin_short = Snackbar.make(ForgotPinChange.this.findViewById(android.R.id.content),
                            "Pin must be 4 characters or more", Snackbar.LENGTH_LONG);
                    sb_pin_short.show();
                }
            }
        });

    }

    //save the global variable values as sharedpreferences
    public static void savePrefs(String newpin) {
        //Save SharedPreferences using 'MyApplication' context--------------------------------------
        MyApplication.mEditor.putString(CURRENT_PIN, newpin);
        MyApplication.mEditor.commit();

    }
}
