package com.thicksandwich.minimalistsavingstracker.initialization;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.R;

public class PinSetup extends AppCompatActivity {

    private static final String TAG = "";
    private EditText pin, verif;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_setup);

        pin = findViewById(R.id.edit_pinsetuppin);
        verif = findViewById(R.id.edit_pinsetupverif);
        Button next = findViewById(R.id.btn_pinsetupnext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin_str = pin.getText().toString();
                String verif_str = verif.getText().toString();
                Log.d(TAG, "PinSetup: " + pin_str + " " + verif_str);

                if(pin_str.equals(verif_str)){
                    //start new activity --> setup security question
                    Intent intent = new Intent(PinSetup.this, TwoFactorSetup.class);
                    intent.putExtra("pin", pin_str);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar sb_pin_error = Snackbar.make(findViewById(android.R.id.content),
                            "New pins do not match", Snackbar.LENGTH_LONG);
                    sb_pin_error.show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
