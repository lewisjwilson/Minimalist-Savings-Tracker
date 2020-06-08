package com.lewiswilson.minimalistsavingstracker;

import android.util.Log;
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
    private RecyclerOnClickListener mrvListener;

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        final TextView mNegativeTextView;
        final TextView mAmountTextView;
        final TextView mReferenceTextView;
        final TextView mDateCategoryTextView;
        RecyclerOnClickListener rvonLongClickListener;

        ItemViewHolder(@NonNull View itemView, RecyclerOnClickListener rvonClickListener) { //onclicklistener passed globally from constructor
            super(itemView);
            mNegativeTextView = itemView.findViewById(R.id.txt_minus_rv);
            mAmountTextView = itemView.findViewById(R.id.txt_entryamount);
            mReferenceTextView = itemView.findViewById(R.id.txt_entryreference);
            mDateCategoryTextView = itemView.findViewById(R.id.txt_category_date);

            this.rvonLongClickListener = rvonClickListener;
            itemView.setOnLongClickListener(this); //setting the recycler onclicklistener
        }

        @Override
        public boolean onLongClick(View v) {
            rvonLongClickListener.RecyclerOnClick(getAdapterPosition()); //get recyclerview position
            return true;
        }
    }

    public RecyclerAdapter(ArrayList<RecyclerItem> itemList, RecyclerOnClickListener rvListener) { //Onclicklistener passed to constructor
        mItemList = itemList;
        this.mrvListener = rvListener; //set to global recyclerview listener variable
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_rv_item, parent, false);
        return new ItemViewHolder(v, mrvListener);
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

        //format the date and category values in the recyclerview
        String unformatted_date_category = currentItem.getDateCategory();
        Log.d("Date Category ", unformatted_date_category);
        String formatted_date = unformatted_date_category.substring(unformatted_date_category.length()-16, unformatted_date_category.length()-6);
        String formatted_category = unformatted_date_category.substring(0, unformatted_date_category.length()-17);
        String formatted_date_category = formatted_date + " / " + formatted_category;

        holder.mDateCategoryTextView.setText(formatted_date_category);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public interface RecyclerOnClickListener { //interprets click
        void RecyclerOnClick(int position); //sends position of clicked item
    }
}
