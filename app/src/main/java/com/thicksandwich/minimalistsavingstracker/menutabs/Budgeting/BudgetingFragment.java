package com.thicksandwich.minimalistsavingstracker.menutabs.Budgeting;

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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.minimalistsavingstracker.BudgetRecyclerAdapter;
import com.thicksandwich.minimalistsavingstracker.BudgetRecyclerItem;
import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class BudgetingFragment extends Fragment implements BudgetRecyclerAdapter.RecyclerOnClickListener {

    private Spinner edit_category;
    private EditText edit_target, edit_targetdp1, edit_targetdp2;
    private TextView txt_budcursymb, txt_buddecimalpoint;
    private Button btn_settarget;
    private ArrayList<BudgetRecyclerItem> budgetItemList = new ArrayList<>();

    public static int year_display;
    public static int month_display;
    public static String display_yearstr;
    public static String display_monthstr;

    public static int current_year;
    public static int current_month;
    public static int current_day;
    public static String current_yearstr;
    public static String current_monthstr;
    public static String current_daystr;

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

        //import database
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());

        //current year and month
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_yearstr = String.valueOf(current_year);
        current_monthstr = String.format("%02d", current_month); //padding with leading 0 where needed
        current_daystr = String.format("%02d", current_day);
        //change textview for year and month
        final TextView txt_year_month = root.findViewById(R.id.txt_budmthyear);
        txt_year_month.setText(current_yearstr + "/" + current_monthstr);

        //values for displaying on recyclerview month selection
        year_display = current_year;
        month_display = current_month;
        display_yearstr = String.valueOf(year_display);
        display_monthstr = String.format("%02d", month_display);

        //find variables
        txt_budcursymb = root.findViewById(R.id.txt_budcursymb);
        txt_buddecimalpoint = root.findViewById(R.id.txt_buddecimalpoint);
        edit_category = root.findViewById(R.id.spn_cat_budget);
        edit_target = root.findViewById(R.id.edit_target);
        edit_targetdp1 = root.findViewById(R.id.edit_targetdp1);
        edit_targetdp2 = root.findViewById(R.id.edit_targetdp2);
        btn_settarget = root.findViewById(R.id.btn_settarget);

        txt_budcursymb.setText(CurrencyFormat.currency_symbol);

        if(CurrencyFormat.decimal_currency){
            edit_target.setVisibility(View.GONE);
        } else {
            edit_targetdp1.setVisibility(View.GONE);
            edit_targetdp2.setVisibility(View.GONE);
            txt_buddecimalpoint.setVisibility(View.GONE);
        }

        Cursor data = myDB.getBudgetData(display_yearstr, display_monthstr);
        DatatoRecycler(data);

        final RecyclerView mRecyclerView = root.findViewById(R.id.recycler_bud);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView.Adapter mAdapter = new BudgetRecyclerAdapter(budgetItemList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        final ArrayList<CharSequence> categories = new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));
        categories.add("Monthly Total"); //add Monthly Total to spinner array
        RefreshAmounts(myDB, categories); //refresh the "amount" values in the budgeting recyclerview
        RefreshView(myDB, mAdapter); //refresh the recyclerview so that is transactions are added, it refreshes on switching to this fragment

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

                String target;
                if(CurrencyFormat.decimal_currency){ //if currency is a decimal type currency
                    if(edit_targetdp2.getText().toString().length()<2){
                        edit_targetdp2.setText("00");
                    }
                    //concatenate edittext fields
                    target = edit_targetdp1.getText().toString() + edit_targetdp2.getText().toString();
                } else {
                    target = edit_target.getText().toString();
                }

                String category = edit_category.getSelectedItem().toString();

                if(target.length() != 0){

                    if(myDB.budgetExists(category, current_yearstr, current_monthstr)){ //if such a record already exists
                        Log.d(TAG, "current month string: " + current_monthstr);
                        myDB.updateBudget(category, target, current_yearstr, current_monthstr);
                        Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Target Updated", Snackbar.LENGTH_LONG);
                        sb.show();
                    } else { //if such a record does not yet exist
                        myDB.addBudget(category, target, current_yearstr, current_monthstr, current_daystr);
                        Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Budget Added", Snackbar.LENGTH_LONG);
                        sb.show();
                    }
                    myDB.getAmountToBudget(category);
                    RefreshAmounts(myDB, categories); //refresh the "amount" values in the budgeting recyclerview
                    RefreshView(myDB, mAdapter);
                    edit_target.setText("");
                    edit_targetdp1.setText("");
                    edit_targetdp2.setText("");

                } else {
                    Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Input a value for Target", Snackbar.LENGTH_LONG);
                    sb.show();
                }
            }
        });



        ImageButton prev_button = root.findViewById(R.id.btn_budmthprev);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(month_display >1){ //changing the year and month values
                    month_display--;
                } else {
                    year_display--;
                    month_display = 12;
                }
                String newyearstr = String.valueOf(year_display);
                String newmonthstr = String.format("%02d", month_display);

                budgetItemList.clear(); //clear current recycler item list
                Cursor data = myDB.getBudgetData(newyearstr, newmonthstr); //get data within new year and month
                DatatoRecycler(data); //add the data to the recycler adapter
                RefreshAmounts(myDB, categories); //refresh the "amount" values in the budgeting recyclerview
                RefreshView(myDB, mAdapter); //refresh the recyclerview so that is transactions are added, it refreshes on switching to this fragment
                mAdapter.notifyDataSetChanged(); //update the recyclerview
                txt_year_month.setText(newyearstr + "/" + newmonthstr); //set textview text

            }
        });

        ImageButton next_button = root.findViewById(R.id.btn_budmthnext);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(month_display <12){ //changing the year and month values
                    month_display++;
                } else {
                    year_display++;
                    month_display = 1;
                }
                String newyearstr = String.valueOf(year_display);
                String newmonthstr = String.format("%02d", month_display);

                budgetItemList.clear(); //clear current recycler item list
                Cursor data = myDB.getBudgetData(newyearstr, newmonthstr); //get data within new year and month
                DatatoRecycler(data); //add the data to the recycler adapter
                RefreshAmounts(myDB, categories); //refresh the "amount" values in the budgeting recyclerview
                RefreshView(myDB, mAdapter); //refresh the recyclerview so that is transactions are added, it refreshes on switching to this fragment
                mAdapter.notifyDataSetChanged(); //update the recyclerview
                txt_year_month.setText(newyearstr + "/" + newmonthstr); //set textview text

            }
        });



        return root;
    }

    public void RefreshAmounts(DatabaseHelper database, ArrayList<CharSequence> category_list){
        //refresh all amounts in recyclerview
        for (int i=0; i<category_list.size(); i++){ //for categories in category_list
            database.getAmountToBudget(category_list.get(i).toString());
        }
    }

    public void RefreshView(DatabaseHelper database, RecyclerView.Adapter adapter){
        budgetItemList.clear();
        display_yearstr = String.valueOf(year_display);
        display_monthstr = String.format("%02d", month_display); //padding with leading 0 where needed
        Cursor data = database.getBudgetData(display_yearstr, display_monthstr);
        DatatoRecycler(data);
        adapter.notifyDataSetChanged();
    }

    public void DatatoRecycler(Cursor data) {
        //adding to db requires: itemList.add(new MainRecyclerItem(int, String));
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
