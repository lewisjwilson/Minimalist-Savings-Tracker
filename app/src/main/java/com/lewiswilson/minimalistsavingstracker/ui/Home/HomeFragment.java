package com.lewiswilson.minimalistsavingstracker.ui.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lewiswilson.MyApplication;
import com.lewiswilson.minimalistsavingstracker.AddTransaction;
import com.lewiswilson.minimalistsavingstracker.DatabaseHelper;
import com.lewiswilson.minimalistsavingstracker.EditBalance;
import com.lewiswilson.minimalistsavingstracker.R;
import com.lewiswilson.minimalistsavingstracker.RecyclerAdapter;
import com.lewiswilson.minimalistsavingstracker.RecyclerItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String BAL_OVERRIDE_KEY = "balance_override";
    public static final String DIFF_KEY = "difference";
    private SharedPreferences sharedPreferences;

    //initialise global variables that need to be passed between methods/classes
    public static int transaction_balance;
    public static int balance_override;
    public static int difference;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //Get SharedPreferences---------------------------------------------------------------------

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        balance_override = sharedPreferences.getInt(BAL_OVERRIDE_KEY, 0);
        difference = sharedPreferences.getInt(DIFF_KEY, 0);

        //Link Database to Arraylist----------------------------------------------------------------
        DatabaseHelper myDB = new DatabaseHelper(getActivity());
        Cursor data = myDB.getData();

        //inflating the recyclerview example item
        View rv_item = inflater.inflate(R.layout.example_rv_item, container, false);
        TextView txt_minus_rv = rv_item.findViewById(R.id.txt_minus_rv);
        ArrayList<RecyclerItem> itemList = new ArrayList<>();

        //adding to db requires: itemList.add(new RecyclerItem(int, String));
        while (data.moveToNext()) {
            if(data.getInt(1) > 0){ //if EXPENSES boolean value is 1 (true) then show minus sign
                txt_minus_rv.setVisibility(View.VISIBLE);
            } else {
                txt_minus_rv.setVisibility(View.INVISIBLE);
            }
            //column index 1 of db = amount, column index 2 = reference
            itemList.add(new RecyclerItem(data.getInt(1), data.getInt(2), data.getString(3),
                    data.getString(4) + " " + data.getString(5)));
        }

        RecyclerView mRecyclerView = root.findViewById(R.id.recycler_income_expenses);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.Adapter mAdapter = new RecyclerAdapter(itemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //sum all transactions in the arraylist together to get the balance-------------------------
        transaction_balance = 0;
        for(int i = 0; i < itemList.size(); i++)
            if(itemList.get(i).getNegative()==1){
                transaction_balance -= itemList.get(i).getAmount(); //if expense, minus the value
            } else {
                transaction_balance += itemList.get(i).getAmount(); //if income, add the value
            }

        //transactions plus the difference between the most recent override
        transaction_balance = transaction_balance + difference;

        //format balance display--------------------------------------------------------------------
        String string_balance = NumberFormat.getNumberInstance(Locale.US).format(transaction_balance); //commas
        TextView edit_balance = root.findViewById(R.id.num_balance);
        String final_value = "¥" + string_balance; //add the yen symbol
        edit_balance.setText(final_value);

        //balance override "edit" click-------------------------------------------------------------
        TextView tv_edit_balance = root.findViewById(R.id.txt_edit_balance);
        tv_edit_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup window to change balance
                startActivity(new Intent(getActivity(), EditBalance.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //add transaction floatingactionbutton click
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransaction.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return root; //return the view
    }

    //override the current balance_override and difference values
    public static void BalanceOverride(int balance_override_val) {

        balance_override = balance_override_val;
        difference = difference + balance_override - transaction_balance;
        savePrefs();

    }

    //save the global variable values as sharedpreferences
    public static void savePrefs() {

        //Save SharedPreferences using 'MyApplication' context--------------------------------------
        MyApplication.mEditor.putInt(BAL_OVERRIDE_KEY, balance_override);
        MyApplication.mEditor.putInt(DIFF_KEY, difference);
        MyApplication.mEditor.commit();

    }

}
