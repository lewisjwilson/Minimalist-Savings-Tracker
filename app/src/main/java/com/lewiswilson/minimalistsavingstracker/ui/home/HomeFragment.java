package com.lewiswilson.minimalistsavingstracker.ui.home;

import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.lewiswilson.minimalistsavingstracker.AddTransaction;
import com.lewiswilson.minimalistsavingstracker.R;
import com.lewiswilson.minimalistsavingstracker.RecyclerAdapter;
import com.lewiswilson.minimalistsavingstracker.RecyclerItem;

import java.math.BigDecimal;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

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

        ArrayList<RecyclerItem> itemList = new ArrayList<>();
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));
        itemList.add(new RecyclerItem(new BigDecimal(1320), "Netflix"));



        mRecyclerView = root.findViewById(R.id.recycler_income_expenses);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerAdapter(itemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

}
