package com.lewiswilson.minimalistsavingstracker;

import java.math.BigDecimal;

public class RecyclerItem { //used to fill the recyclerview
    private BigDecimal mAmount;
    private String mReference;

    public RecyclerItem(BigDecimal amount, String reference) { //define an item
         mAmount = amount;
         mReference = reference;
    }

    public BigDecimal getAmount() { //getting the amount value
        return mAmount;
    }

    public String getReference() { //getting the reference value
        return mReference;
    }
}
