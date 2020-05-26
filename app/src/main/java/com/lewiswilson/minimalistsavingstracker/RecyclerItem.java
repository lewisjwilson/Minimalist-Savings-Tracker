package com.lewiswilson.minimalistsavingstracker;

public class RecyclerItem { //used to fill the recyclerview
    private final int mNegative;
    private final int mAmount;
    private final String mReference;

    public RecyclerItem(int negative, int amount, String reference) { //define an item
         mNegative = negative; //1 means minus sign should show, 0 means the opposite
         mAmount = amount;
         mReference = reference;
    }

    public int getNegative() { //getting the reference value
        return mNegative;
    }

    public int getAmount() { //getting the amount value
        return mAmount;
    }

    String getReference() { //getting the reference value
        return mReference;
    }
}
