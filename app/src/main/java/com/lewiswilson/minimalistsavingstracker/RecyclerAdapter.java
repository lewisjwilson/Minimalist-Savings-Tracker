package com.lewiswilson.minimalistsavingstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> { //links rv with arraylist

    private final ArrayList<RecyclerItem> mItemList;

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView mNegativeTextView;
        final TextView mAmountTextView;
        final TextView mReferenceTextView;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mNegativeTextView = itemView.findViewById(R.id.txt_minus_rv);
            mAmountTextView = itemView.findViewById(R.id.txt_entryamount);
            mReferenceTextView = itemView.findViewById(R.id.txt_entryreference);
        }
    }

    public RecyclerAdapter(ArrayList<RecyclerItem> itemList) {
        mItemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_rv_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) { //int is position of item we are binding
        RecyclerItem currentItem = mItemList.get(position);

        int negative = currentItem.getNegative();
        if(negative==1){//if item is an expense
            holder.mNegativeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mNegativeTextView.setVisibility(View.INVISIBLE);
        }


        //format for commas between each 3 zeros
        String amount_display = NumberFormat.getNumberInstance(Locale.US).format(currentItem.getAmount());
        String final_display = "¥" + amount_display;
        holder.mAmountTextView.setText(final_display);
        holder.mReferenceTextView.setText(currentItem.getReference());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
