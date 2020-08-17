package com.thicksandwich.minimalistsavingstracker;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

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
        String amount_display = moneyFormat(currentItem.getAmount());
        holder.mAmountTextView.setText(amount_display);

        //format for commas between each 3 zeros
        String target_display = moneyFormat(currentItem.getTarget());
        holder.mTargetTextView.setText(target_display);

        int level = 0;
        if(currentItem.getAmount()!=0) { //setting background percentage (using bg_level.xml) on the relative view
            float amount = (float)currentItem.getAmount();
            float target = (float)currentItem.getTarget();
            level = (int) ((amount / target) * 10000); //0 = 0%, 10000 = 100%
        }

        holder.budget_item_view.getBackground().setLevel(level);

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        final TextView mCategoryTextView;
        final TextView mAmountTextView;
        final TextView mTargetTextView;
        RecyclerOnClickListener rvonLongClickListener;
        final RelativeLayout budget_item_view;

        ItemViewHolder(@NonNull View itemView, RecyclerOnClickListener rvonClickListener) { //onclicklistener passed globally from constructor
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.txt_budrvcat);
            mAmountTextView = itemView.findViewById(R.id.txt_budrvamount);
            mTargetTextView = itemView.findViewById(R.id.txt_budrvtarget);
            budget_item_view = itemView.findViewById(R.id.budget_item_view);

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
