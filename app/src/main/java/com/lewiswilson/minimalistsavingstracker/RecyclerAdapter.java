package com.lewiswilson.minimalistsavingstracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> { //links rv with arraylist

    private ArrayList<RecyclerItem> mItemList;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mAmountTextView;
        public TextView mReferenceTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
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
        ItemViewHolder ivh = new ItemViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) { //int is position of item we are binding
        RecyclerItem currentItem = mItemList.get(position);

        holder.mAmountTextView.setText(String.valueOf(currentItem.getAmount()));
        holder.mReferenceTextView.setText(currentItem.getReference());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
