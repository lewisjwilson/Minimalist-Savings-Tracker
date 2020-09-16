package com.thicksandwich.minimalistsavingstracker.menutabs.StandingOrders;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class StOrdRecyclerAdapter extends RecyclerView.Adapter<StOrdRecyclerAdapter.ItemViewHolder> { //links rv with arraylist

    private final ArrayList<StOrdRecyclerItem> mItemList;
    private RecyclerOnClickListener mrvListener;

    public StOrdRecyclerAdapter(ArrayList<StOrdRecyclerItem> itemList, RecyclerOnClickListener rvListener) { //Onclicklistener passed to constructor
        mItemList = itemList;
        this.mrvListener = rvListener; //set to global recyclerview listener variable
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stord_rv_item, parent, false);
        return new ItemViewHolder(v, mrvListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) { //int is position of item we are binding
        StOrdRecyclerItem currentItem = mItemList.get(position);

        int negative = currentItem.getNegative();
        if(negative==1){//if item is an expense
            holder.mNegativeTextView.setVisibility(View.VISIBLE);
            //holder.main_rvitem_view.setBackgroundColor(Color.parseColor("#1EB82504"));
        } else {
            holder.mNegativeTextView.setVisibility(View.INVISIBLE);
            //holder.main_rvitem_view.setBackgroundColor(Color.parseColor("#1E0AC800"));
        }

        //format for commas between each 3 zeros
        String amount_display = moneyFormat(currentItem.getAmount());
        holder.mAmountTextView.setText(amount_display);
        holder.mReferenceTextView.setText(currentItem.getReference());
        holder.mRecurDayTextView.setText(currentItem.getRecurDay());
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        final TextView mNegativeTextView;
        final TextView mAmountTextView;
        final TextView mReferenceTextView;
        final TextView mRecurDayTextView;
        RecyclerOnClickListener rvonLongClickListener;
        final RelativeLayout main_rvitem_view;

        ItemViewHolder(@NonNull View itemView, RecyclerOnClickListener rvonClickListener) { //onclicklistener passed globally from constructor
            super(itemView);
            mNegativeTextView = itemView.findViewById(R.id.txt_minus_rv);
            mAmountTextView = itemView.findViewById(R.id.txt_entryamount);
            mReferenceTextView = itemView.findViewById(R.id.txt_entryreference);
            mRecurDayTextView = itemView.findViewById(R.id.txt_recurday);
            main_rvitem_view = itemView.findViewById(R.id.main_rvitem_view);

            this.rvonLongClickListener = rvonClickListener;
            itemView.setOnLongClickListener(this); //setting the recycler onclicklistener
        }

        @Override
        public boolean onLongClick(View v) {
            rvonLongClickListener.RecyclerOnClick(getAdapterPosition()); //get recyclerview position
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public interface RecyclerOnClickListener { //interprets click
        void onCreate(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

        void RecyclerOnClick(int position); //sends position of clicked item
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
}
