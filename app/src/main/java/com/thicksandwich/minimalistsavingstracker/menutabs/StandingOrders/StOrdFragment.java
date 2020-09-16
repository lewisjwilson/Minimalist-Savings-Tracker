package com.thicksandwich.minimalistsavingstracker.menutabs.StandingOrders;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.DeleteDialog;
import com.thicksandwich.minimalistsavingstracker.MainActivity;
import com.thicksandwich.minimalistsavingstracker.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class StOrdFragment extends Fragment implements StOrdRecyclerAdapter.RecyclerOnClickListener{

    public static boolean delete_clicked;
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private ArrayList<StOrdRecyclerItem> displayedItemList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StOrdViewModel stOrdViewModel = ViewModelProviders.of(this).get(StOrdViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_standing_orders, container, false);
        final TextView textView = root.findViewById(R.id.text_stord);
        stOrdViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //linking xml items-------------------------------------------------------------------------
        mRecyclerView = root.findViewById(R.id.rv_standingorders);
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());
        Cursor data = myDB.getStandingOrders(); //add data from database into recyclerview


        //inflating the recyclerview example item and attaching the data to the adapter-------------
        View rv_item = inflater.inflate(R.layout.stord_rv_item, container, false);
        final TextView txt_minus_rv = rv_item.findViewById(R.id.txt_minus_rv);
        StOrdtoRecycler(data, txt_minus_rv); //get database data and put into recyclerview

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new StOrdRecyclerAdapter(displayedItemList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return root; //return the view
    }

    public void StOrdtoRecycler(Cursor data, TextView txt_minus_rv) {
        //adding to db requires: itemList.add(new StOrdRecyclerItem(int, String));
        while (data.moveToNext()) {
            if(data.getInt(1) > 0){ //if EXPENSES boolean value is 1 (true) then show minus sign
                txt_minus_rv.setVisibility(View.VISIBLE);
            } else {
                txt_minus_rv.setVisibility(View.INVISIBLE);
            }

            String recur_day_text = "Day of Month: " + data.getString(7);

            Log.d("TAG", "StOrdtoRecycler getRef: " + data.getString(3));
            //column index 0 of db = id, column index 1 of db = expense, column index 2 = amount...
            displayedItemList.add(new StOrdRecyclerItem(data.getLong(0), data.getInt(1), data.getInt(2),
                    data.getString(3), recur_day_text));
        }
    }

    @Override
    public void RecyclerOnClick(final int position) {
        long databaseid = displayedItemList.get(position).getID(); //get the database id of the clicked item
        Log.d(TAG, "RecyclerOnClick: " + position + " clicked");
        Bundle args = new Bundle();
        args.putLong("id", databaseid);
        args.putInt("table", 3);
        DeleteDialog dialog = new DeleteDialog();
        dialog.setArguments(args);

        ///when dialog is dismissed, refesh mainactivity
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                //if the delete button was clicked, remove list item
                if(delete_clicked) {
                    displayedItemList.remove(position);
                    mRecyclerView.removeViewAt(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, displayedItemList.size());
                    mAdapter.notifyDataSetChanged();
                    delete_clicked = false; //reset "clicked" status
                }

            }
        });

        dialog.show(getActivity().getSupportFragmentManager(), "deletedialog");

    }


    @Override
    public void onCreate(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {}
}
