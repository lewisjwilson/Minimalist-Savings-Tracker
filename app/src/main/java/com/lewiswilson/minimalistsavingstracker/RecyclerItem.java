package com.lewiswilson.minimalistsavingstracker;

public class RecyclerItem { //used to fill the recyclerview
    private int mAmount;
    private String mReference;

    public RecyclerItem(int amount, String reference) { //define an item
         mAmount = amount;
         mReference = reference;
    }

    public int getAmount() { //getting the amount value
        return mAmount;
    }

    public String getReference() { //getting the reference value
        return mReference;
    }
}
