package com.thicksandwich.minimalistsavingstracker.changelogin;

import android.content.SharedPreferences;
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
import com.thicksandwich.MyApplication;
import com.thicksandwich.minimalistsavingstracker.R;

import static android.content.ContentValues.TAG;

public class ChangeTwoFactor extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    public static final String SEC_QUESTION = "security_question";
    public static final String SEC_ANSWER = "security_answer";
    public static String current_pin;
    private SharedPreferences sharedPreferences;

    private Spinner questions;

    public static ArrayAdapter<CharSequence> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_twofactor);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        current_pin = sharedPreferences.getString(CURRENT_PIN, "0000"); //default pin is 0000

        final EditText pin = findViewById(R.id.edit_twofactor_pin);
        final EditText security_answer = findViewById(R.id.edit_newanswer);
        final EditText security_verif = findViewById(R.id.edit_newanswerverif);
        questions = findViewById(R.id.spn_changequestions);
        Button btn_changepin = findViewById(R.id.btn_forgotpinchange);

        //fill spinner with arraydata from strings
        adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questions.setAdapter(adapter);
        questions.setOnItemSelectedListener(this);

        btn_changepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pin_str = pin.getText().toString();
                String answer_str = security_answer.getText().toString();
                String verif_str = security_verif.getText().toString();
                String question_str = questions.getSelectedItem().toString();

                if(pin_str.equals(current_pin)){
                    if(answer_str.length()>=4) {
                        if (answer_str.equals(verif_str)) {
                            //change pin to newpin in SharedPreferences
                            savePrefs(question_str, answer_str);

                            Snackbar sb_newpin_change = Snackbar.make(findViewById(android.R.id.content),
                                    "Pin Changed", Snackbar.LENGTH_LONG);
                            sb_newpin_change.show();

                            //go back to previous activity
                            onBackPressed();

                        } else {
                            Snackbar sb_newpin_error = Snackbar.make(findViewById(android.R.id.content),
                                    "New answers do not match", Snackbar.LENGTH_LONG);
                            sb_newpin_error.show();
                        }
                    } else {
                        Snackbar sb_twofactor_short = Snackbar.make(findViewById(android.R.id.content),
                                "Security question answer must be 4 characters or more", Snackbar.LENGTH_LONG);
                        sb_twofactor_short.show();
                    }
                } else {
                    Snackbar sb_pin_error = Snackbar.make(findViewById(android.R.id.content),
                            "Pin is incorrect", Snackbar.LENGTH_LONG);
                    sb_pin_error.show();
                }

            }
        });


    }

    //save the global variable values as sharedpreferences
    public static void savePrefs(String question, String answer) {
        //Save SharedPreferences using 'MyApplication' context--------------------------------------
        MyApplication.mEditor.putString(SEC_QUESTION, question);
        MyApplication.mEditor.putString(SEC_ANSWER, answer);
        MyApplication.mEditor.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); //press the back button
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}