package com.lewiswilson.minimalistsavingstracker.ui.Budgeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.lewiswilson.minimalistsavingstracker.DatabaseHelper;
import com.lewiswilson.minimalistsavingstracker.R;

import java.util.ArrayList;
import java.util.Arrays;

public class BudgetingFragment extends Fragment {

    private Spinner edit_category;
    private EditText edit_target;
    private Button btn_settarget;

    //public static ArrayAdapter<CharSequence> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        BudgetingViewModel galleryViewModel = ViewModelProviders.of(this).get(BudgetingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_budgeting, container, false);
        final TextView textView = root.findViewById(R.id.text_budgeting);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //import database
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());

        //find variables
        edit_category = root.findViewById(R.id.spn_cat_budget);
        edit_target = root.findViewById(R.id.edit_target);
        btn_settarget = root.findViewById(R.id.btn_settarget);

        //on "Set Target" button click
        btn_settarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Added Category: " + edit_category.getSelectedItem().toString() + "\n" +
                        "with Target: " + edit_target.getText().toString(), Snackbar.LENGTH_LONG);
                sb.show();
            }
        });

        ArrayList<CharSequence> categories = new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));
        categories.add("Monthly Total"); //add Monthly Total to spinner array

        //fill spinner with arraydata from strings
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_category.setAdapter(adapter);
        edit_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return root;
    }
}
