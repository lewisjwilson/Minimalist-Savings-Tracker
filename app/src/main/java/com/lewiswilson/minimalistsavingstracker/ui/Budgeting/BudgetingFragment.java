package com.lewiswilson.minimalistsavingstracker.ui.Budgeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.lewiswilson.minimalistsavingstracker.R;

public class BudgetingFragment extends Fragment {

    private Spinner edit_category;

    public static ArrayAdapter<CharSequence> adapter;

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

        edit_category = root.findViewById(R.id.spn_cat_budget);

        //fill spinner with arraydata from strings
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.expense_categories, android.R.layout.simple_spinner_item);
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
