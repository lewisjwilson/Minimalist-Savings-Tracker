package com.thicksandwich.minimalistsavingstracker.menutabs.Home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thicksandwich.MyApplication;
import com.thicksandwich.minimalistsavingstracker.AddTransaction;
import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.DeleteDialog;
import com.thicksandwich.minimalistsavingstracker.EditBalance;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;
import com.thicksandwich.minimalistsavingstracker.MainActivity;
import com.thicksandwich.minimalistsavingstracker.R;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements MainRecyclerAdapter.RecyclerOnClickListener {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FIRST_TIME = "first_time";
    public static final String CURRENCY = "currency";
    public static final String BAL_OVERRIDE_KEY = "balance_override";
    public static final String DIFF_KEY = "difference";
    private SharedPreferences sharedPreferences;

    private ArrayList<MainRecyclerItem> displayedItemList = new ArrayList<>();

    //initialise global variables that need to be passed between methods/classes
    public static int transaction_balance;
    public static int balance_override;
    public static int difference;
    public static int yearint;
    public static int monthint;

    public static String currency_code;
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        new CurrencyFormat();

        //linking xml items-------------------------------------------------------------------------
        final ImageButton btn_hints = root.findViewById(R.id.btn_hints);
        final TextView txt_year_month = root.findViewById(R.id.txt_year_month);
        final RecyclerView mRecyclerView = root.findViewById(R.id.recycler_income_expenses);
        TextView edit_balance = root.findViewById(R.id.num_balance);
        final TextView tv_edit_balance = root.findViewById(R.id.txt_edit_balance);
        ImageButton prev_month = root.findViewById(R.id.btn_month_prev);
        ImageButton next_month = root.findViewById(R.id.btn_month_next);
        final FloatingActionButton fab = root.findViewById(R.id.fab);

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //check for first time run
        if(sharedPreferences.getBoolean(FIRST_TIME, false)){ //if first time
            Log.d(TAG, "SHAREDPREFERENCES: First time run");
            firstTimeHints(getActivity(), fab, tv_edit_balance, btn_hints);
            sharedPreferences.edit().putBoolean(FIRST_TIME, false).commit();
        }
        currency_code = sharedPreferences.getString(CURRENCY, "?");
        balance_override = sharedPreferences.getInt(BAL_OVERRIDE_KEY, 0);
        difference = sharedPreferences.getInt(DIFF_KEY, 0);

        btn_hints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstTimeHints(getActivity(), fab, tv_edit_balance, btn_hints);
            }
        });

        //Link Database to Arraylist----------------------------------------------------------------
        //current year and month
        yearint = Calendar.getInstance().get(Calendar.YEAR);
        monthint = Calendar.getInstance().get(Calendar.MONTH) + 1;
        final String yearstr = String.valueOf(yearint);
        final String monthstr = String.format("%02d", monthint); //padding with leading 0 where needed
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());
        Cursor data = myDB.getDisplayData(yearstr, monthstr); //add data from database into recyclerview

        //change textview for year and month
        txt_year_month.setText(yearstr + "/" + monthstr);

        //inflating the recyclerview example item and attaching the data to the adapter-------------
        View rv_item = inflater.inflate(R.layout.example_rv_item, container, false);
        final TextView txt_minus_rv = rv_item.findViewById(R.id.txt_minus_rv);
        DatatoRecycler(data, txt_minus_rv); //get database data and put into recyclerview

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView.Adapter mAdapter = new MainRecyclerAdapter(displayedItemList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        //sum all transactions in the arraylist together to get the balance-------------------------
        transaction_balance = myDB.sumTransactions();
        //transactions plus the difference between the most recent override
        transaction_balance = transaction_balance + difference;

        String final_value = moneyFormat(transaction_balance); //commas
        edit_balance.setText(final_value);

        //balance override "edit" click-------------------------------------------------------------
        tv_edit_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup window to change balance
                startActivity(new Intent(getActivity(), EditBalance.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //previous month button click
        prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monthint>1){ //changing the year and month values
                    monthint--;
                } else {
                    yearint--;
                    monthint = 12;
                }
                String newyearstr = String.valueOf(yearint);
                String newmonthstr = String.format("%02d", monthint);

                displayedItemList.clear(); //clear current recycler item list
                Cursor data = myDB.getDisplayData(newyearstr, newmonthstr); //get data within new year and month
                DatatoRecycler(data, txt_minus_rv); //add the data to the recycler adapter
                mAdapter.notifyDataSetChanged(); //update the recyclerview
                Log.d(TAG, "onClick: " + monthint);
                txt_year_month.setText(newyearstr + "/" + newmonthstr); //set textview text
            }
        });

        //next month button click
        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monthint<12){ //changing the year and month values
                    monthint++;
                } else {
                    yearint++;
                    monthint = 1;
                }
                String newyearstr = String.valueOf(yearint);
                String newmonthstr = String.format("%02d", monthint);

                displayedItemList.clear(); //clear current recycler item list
                Cursor data = myDB.getDisplayData(newyearstr, newmonthstr); //get data within new year and month
                DatatoRecycler(data, txt_minus_rv); //add the data to the recycler adapter
                mAdapter.notifyDataSetChanged(); //update the recyclerview
                Log.d(TAG, "onClick: " + monthint);
                txt_year_month.setText(newyearstr + "/" + newmonthstr); //set textview text
            }
        });

        //add transaction floatingactionbutton click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransaction.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return root; //return the view
    }

    public void DatatoRecycler(Cursor data, TextView txt_minus_rv) {
        //adding to db requires: itemList.add(new MainRecyclerItem(int, String));
        while (data.moveToNext()) {
            if(data.getInt(1) > 0){ //if EXPENSES boolean value is 1 (true) then show minus sign
                txt_minus_rv.setVisibility(View.VISIBLE);
            } else {
                txt_minus_rv.setVisibility(View.INVISIBLE);
            }
            //column index 0 of db = id, column index 1 of db = expense, column index 2 = amount...
            displayedItemList.add(new MainRecyclerItem(data.getLong(0), data.getInt(1), data.getInt(2), data.getString(3),
                    data.getString(4) + " " + data.getString(5)));
        }
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


    @Override
    public void RecyclerOnClick(int position) {
        long databaseid = displayedItemList.get(position).getID(); //get the database id of the clicked item
        Log.d(TAG, "RecyclerOnClick: " + position + " clicked");
        Bundle args = new Bundle();
        args.putLong("id", databaseid);
        DeleteDialog dialog = new DeleteDialog();
        dialog.setArguments(args);

        //when dialog is dismissed, refesh mainactivity
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });

        dialog.show(getActivity().getSupportFragmentManager(), "deletedialog");

    }

    public String moneyFormat(int i){
        BigDecimal output;

        if(CurrencyFormat.decimal_currency) {
            output = new BigDecimal(BigInteger.valueOf(i), 2);
        } else {
            output = new BigDecimal(BigInteger.valueOf(i));
        }
        return CurrencyFormat.cf.format(output); //format currency

    }

    public void firstTimeHints(Activity activity, FloatingActionButton addTrans, TextView edit, ImageButton hints){

        final FancyShowCaseView intro = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHintBg))
                .title("Welcome to MST. Here are a few hints to get you started.")
                .titleStyle(R.style.HintsStyle, Gravity.CENTER)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView fab = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHintBg))
                .focusOn(addTrans)
                .title("Use this button to add a new income or expense")
                .titleStyle(R.style.HintsStyle, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();

        final FancyShowCaseView edit_amount = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHintBg))
                .focusOn(edit)
                .title("If you need to edit your balance at any time, click here")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();


        final FancyShowCaseView again = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHintBg))
                .focusOn(hints)
                .title("To see these hints again, please use this button.")
                .titleStyle(R.style.HintsStyle, Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();

        FancyShowCaseQueue mQueue = new FancyShowCaseQueue()
                .add(intro)
                .add(fab)
                .add(edit_amount)
                .add(again);

        mQueue.show();
    }


}
