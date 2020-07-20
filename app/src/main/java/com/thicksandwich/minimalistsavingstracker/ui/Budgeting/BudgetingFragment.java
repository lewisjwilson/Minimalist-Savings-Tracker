package com.thicksandwich.minimalistsavingstracker.ui.Budgeting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.BudgetRecyclerAdapter;
import com.thicksandwich.minimalistsavingstracker.BudgetRecyclerItem;
import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.MainActivity;
import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.RecyclerAdapter;
import com.thicksandwich.minimalistsavingstracker.RecyclerItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class BudgetingFragment extends Fragment implements BudgetRecyclerAdapter.RecyclerOnClickListener {

    private Spinner edit_category;
    private EditText edit_target;
    private Button btn_settarget;

    private ArrayList<BudgetRecyclerItem> budgetItemList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        BudgetingViewModel galleryViewModel = ViewModelProviders.of(this).get(BudgetingViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_budgeting, container, false);
        final TextView textView = root.findViewById(R.id.text_budgeting);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //getting current month and year
        DateFormat dateFormatDay = new SimpleDateFormat("dd");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        DateFormat dateFormatYear = new SimpleDateFormat("YYYY");
        Date date = new Date();
        String day = dateFormatDay.format(date); //set current month as targetmonth
        final String month = dateFormatMonth.format(date); //set current month as targetmonth
        final String year = dateFormatYear.format(date); //set current year
        final String targetmonth = year + "-" + month + "-" + day;

        //import database
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());

        //find variables
        edit_category = root.findViewById(R.id.spn_cat_budget);
        edit_target = root.findViewById(R.id.edit_target);
        btn_settarget = root.findViewById(R.id.btn_settarget);
        Cursor data = myDB.getBudgetData(targetmonth);
        DatatoRecycler(data);

        final RecyclerView mRecyclerView = root.findViewById(R.id.recycler_bud);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView.Adapter mAdapter = new BudgetRecyclerAdapter(budgetItemList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


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

        //on "Set Target" button click
        btn_settarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = edit_category.getSelectedItem().toString();
                String target = edit_target.getText().toString();

                if(edit_target.length() != 0){

                    if(myDB.budgetExists(category, year, month)){ //if such a record already exists
                        myDB.updateBudget(category, target, year, month);
                        Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Target Updated", Snackbar.LENGTH_LONG);
                        sb.show();
                    } else { //if such a record does not yet exist
                        myDB.addBudget(category, target, targetmonth);
                        Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Budget Added", Snackbar.LENGTH_LONG);
                        sb.show();
                    }
                    myDB.getAmountToBudget(category);

                } else {
                    Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Input a value for Target", Snackbar.LENGTH_LONG);
                    sb.show();
                }

            }
        });
        return root;
    }

    public void DatatoRecycler(Cursor data) {
        //adding to db requires: itemList.add(new RecyclerItem(int, String));
        while (data.moveToNext()) {
            //column index 0 of db = id, column index 1 of db = category, column index 2 = amount...
            budgetItemList.add(new BudgetRecyclerItem(data.getLong(0), //id
                    data.getString(1), //category
                    data.getInt(3), //amount
                    data.getInt(2))); //target
        }
    }

    @Override
    public void RecyclerOnClick(int position) { //when recyclerview is clicked
        long databaseid = budgetItemList.get(position).getID(); //get the database id of the clicked item
        Log.d(TAG, "RecyclerOnClick: " + position + " clicked");
    }
}
