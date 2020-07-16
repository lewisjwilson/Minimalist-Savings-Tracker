package com.thicksandwich.minimalistsavingstracker;

public class BudgetRecyclerItem { //used to fill the recyclerview
    private final long mID;
    private final String mCategory;
    private final int mAmount;
    private final int mTarget;

    public BudgetRecyclerItem(long id, String category, int amount, int target) { //define an item
        mID = id;
        mCategory = category;
        mAmount = amount;
        mTarget = target;
    }
    public long getID() {
        return mID;
    }

    public String getCategory() { //getting the amount value
        return mCategory;
    }

    public int getAmount() { //getting the amount value
        return mAmount;
    }

    public int getTarget() { //getting the amount value
        return mTarget;
    }

}
