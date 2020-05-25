package com.lewiswilson.minimalistsavingstracker.ui.home;

import java.lang.*;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lewiswilson.minimalistsavingstracker.AddTransaction;
import com.lewiswilson.minimalistsavingstracker.DatabaseHelper;
import com.lewiswilson.minimalistsavingstracker.R;
import com.lewiswilson.minimalistsavingstracker.RecyclerAdapter;
import com.lewiswilson.minimalistsavingstracker.RecyclerItem;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    DatabaseHelper myDB;
    private HomeViewModel homeViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;  //bridge between recyclerview and arraylist
    //(can't load all items at once - provides number of
    //items we currently need)
    private RecyclerView.LayoutManager mLayoutManager; //aligns items in list

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransaction.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        //get database data and populate recyclerview
        myDB = new DatabaseHelper(getActivity());
        Cursor data = myDB.getData();

        ArrayList<RecyclerItem> itemList = new ArrayList<>();

        //adding to db requires: itemList.add(new RecyclerItem(int, String));
        while (data.moveToNext()) {
            //column index 1 of db = amount, column index 2 = reference
            itemList.add(new RecyclerItem(data.getInt(1), data.getString(2)));
        }


        mRecyclerView = root.findViewById(R.id.recycler_income_expenses);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerAdapter(itemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //iterate through arraylist and add all amounts together
        int balance = 0;
        for(int i = 0; i < itemList.size(); i++)
           balance += itemList.get(i).getAmount();

        //format for commas between each 3 zeros
        String string_balance = NumberFormat.getNumberInstance(Locale.US).format(balance);
        TextView edit_balance = root.findViewById(R.id.num_balance);
        String final_value = "¥" + string_balance;
        edit_balance.setText(final_value);

        return root;
    }


}
