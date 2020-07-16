package com.thicksandwich.minimalistsavingstracker;

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

public class BudgetRecyclerAdapter extends RecyclerView.Adapter<BudgetRecyclerAdapter.ItemViewHolder> { //links rv with arraylist

    private final ArrayList<BudgetRecyclerItem> mItemList;
    private RecyclerOnClickListener budrvListener;

    public BudgetRecyclerAdapter(ArrayList<BudgetRecyclerItem> itemList, BudgetRecyclerAdapter.RecyclerOnClickListener rvListener) { //Onclicklistener passed to constructor
        mItemList = itemList;
        this.budrvListener = rvListener; //set to global recyclerview listener variable
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_rv_item, parent, false);
        return new ItemViewHolder(v, budrvListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) { //int is position of item we are binding
        BudgetRecyclerItem currentItem = mItemList.get(position);

        holder.mCategoryTextView.setText(currentItem.getCategory());

        //format for commas between each 3 zeros
        String amount_display = NumberFormat.getNumberInstance(Locale.US).format(currentItem.getAmount());
        holder.mAmountTextView.setText(amount_display);

        //format for commas between each 3 zeros
        String target_display = NumberFormat.getNumberInstance(Locale.US).format(currentItem.getTarget());
        holder.mTargetTextView.setText(target_display);

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        final TextView mCategoryTextView;
        final TextView mAmountTextView;
        final TextView mTargetTextView;
        RecyclerOnClickListener rvonLongClickListener;

        ItemViewHolder(@NonNull View itemView, RecyclerOnClickListener rvonClickListener) { //onclicklistener passed globally from constructor
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.txt_budrvcat);
            mAmountTextView = itemView.findViewById(R.id.txt_budrvamount);
            mTargetTextView = itemView.findViewById(R.id.txt_budrvtarget);

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
        void RecyclerOnClick(int position); //sends position of clicked item
    }
}
