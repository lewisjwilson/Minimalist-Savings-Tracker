package com.thicksandwich.minimalistsavingstracker.initialization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.MyApplication;
import com.thicksandwich.minimalistsavingstracker.MainActivity;
import com.thicksandwich.minimalistsavingstracker.R;

public class TwoFactorSetup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CURRENT_PIN = "current_pin";
    public static final String SEC_QUESTION = "security_question";
    public static final String SEC_ANSWER = "security_answer";
    private static final String TAG = "";

    private SharedPreferences sharedPreferences;

    private Spinner spn_questions;
    private EditText answer, verif;
    public static ArrayAdapter<CharSequence> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twofactor_setup);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        final String pin = getIntent().getStringExtra("pin"); //get pin from PinSetup.java

        spn_questions = findViewById(R.id.spn_changequestions);
        answer = findViewById(R.id.edit_twofactoranswer);
        verif = findViewById(R.id.edit_twofactorverif);
        Button submit = findViewById(R.id.btn_twofactorsubmit);

        //fill spinner with arraydata from strings
        adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_questions.setAdapter(adapter);
        spn_questions.setOnItemSelectedListener(this);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = spn_questions.getSelectedItem().toString();
                String answer_str = answer.getText().toString();
                String verif_str = verif.getText().toString();

                if(answer_str.equals(verif_str)){
                    //save pin and answer to sharedpreferences
                    savePrefs(pin, question, answer_str);


                    startActivity(new Intent(TwoFactorSetup.this, MainActivity.class));
                    finish();

                } else {
                    Snackbar sb_twofactor_error = Snackbar.make(findViewById(android.R.id.content),
                            "Answers do not match", Snackbar.LENGTH_LONG);
                    sb_twofactor_error.show();
                }

            }
        });

    }

    //save the global variable values as sharedpreferences
    public static void savePrefs(String pin, String question, String answer) {
        //Save SharedPreferences using 'MyApplication' context--------------------------------------
        MyApplication.mEditor.putString(CURRENT_PIN, pin);
        MyApplication.mEditor.putString(SEC_QUESTION, question);
        MyApplication.mEditor.putString(SEC_ANSWER, answer);
        MyApplication.mEditor.commit();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
