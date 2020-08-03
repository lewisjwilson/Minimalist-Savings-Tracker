package com.thicksandwich.minimalistsavingstracker.forgotpin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.Authentication;
import com.thicksandwich.minimalistsavingstracker.ChangePin;
import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.initialization.PinSetup;

import org.w3c.dom.Text;

public class ForgotPin extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SEC_QUESTION = "security_question";
    public static final String SEC_ANSWER = "security_answer";
    private SharedPreferences sharedPreferences;

    EditText answer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pin);

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String question = sharedPreferences.getString(SEC_QUESTION, "question_not_set"); //default pin is 0000

        TextView chosen_question = findViewById(R.id.txt_forgotchosenq);
        chosen_question.setText(question);
        answer = findViewById(R.id.edit_forgotanswer);
        Button submit = findViewById(R.id.btn_forgot);

        if (question.equals("question_not_set")){
            submit.setEnabled(false); //disable submit button if not question has not been set somehow
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_answer = answer.getText().toString();
                String true_answer = sharedPreferences.getString(SEC_ANSWER, null); //default answer is null
                if(user_answer.equals(true_answer)){
                    Intent intent = new Intent(ForgotPin.this, ForgotPinChange.class);
                    startActivity(intent);
                } else {
                    Snackbar sb_forgot_error = Snackbar.make(findViewById(android.R.id.content),
                            "Answer is incorrect", Snackbar.LENGTH_LONG);
                    sb_forgot_error.show();
                }

            }
        });


    }
}
