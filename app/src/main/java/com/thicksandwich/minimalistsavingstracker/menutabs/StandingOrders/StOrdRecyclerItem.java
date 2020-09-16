package com.thicksandwich.minimalistsavingstracker.menutabs.StandingOrders;

public class StOrdRecyclerItem { //used to fill the recyclerview
    private final long mID;
    private final int mNegative;
    private final int mAmount;
    private final String mReference;
    private final String mRecurDay;

    public StOrdRecyclerItem(long id, int negative, int amount, String reference, String recur_day) { //define an item
        mID = id;
        mNegative = negative; //1 means minus sign should show, 0 means the opposite
        mAmount = amount;
        mReference = reference;
        mRecurDay = recur_day;
    }
    public long getID() {
        return mID;
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

    public String getRecurDay() {
        return mRecurDay;
    }
}
