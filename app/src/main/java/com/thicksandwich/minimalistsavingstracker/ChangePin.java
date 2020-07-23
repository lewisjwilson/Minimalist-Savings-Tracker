package com.thicksandwich.minimalistsavingstracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.MyApplication;

import static android.content.ContentValues.TAG;

public class ChangePin extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    public static String current_pin;
    private SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pin);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        current_pin = sharedPreferences.getString(CURRENT_PIN, "0000"); //default pin is 0000
        Log.d(TAG, "current pin: " + CURRENT_PIN);


        final EditText edit_oldpin = findViewById(R.id.edit_oldpin);
        final EditText edit_newpin = findViewById(R.id.edit_newpin);
        final EditText edit_verifpin = findViewById(R.id.edit_newpinverif);
        Button btn_changepin = findViewById(R.id.btn_change_pin);

        btn_changepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldpin = edit_oldpin.getText().toString();
                String newpin = edit_newpin.getText().toString();
                String verifpin = edit_verifpin.getText().toString();

                if(oldpin.equals(current_pin)){
                    if(newpin.equals(verifpin)){
                        //change pin to newpin in SharedPreferences
                        savePrefs(newpin);

                        Snackbar sb_newpin_change = Snackbar.make(findViewById(android.R.id.content),
                                "Pin Changed", Snackbar.LENGTH_LONG);
                        sb_newpin_change.show();

                        //go back to previous activity
                        onBackPressed();

                    } else {
                        Snackbar sb_newpin_error = Snackbar.make(findViewById(android.R.id.content),
                                "New pins do not match", Snackbar.LENGTH_LONG);
                        sb_newpin_error.show();
                    }
                } else {
                    Snackbar sb_oldpin_error = Snackbar.make(findViewById(android.R.id.content),
                            "Old pin is incorrect", Snackbar.LENGTH_LONG);
                    sb_oldpin_error.show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}