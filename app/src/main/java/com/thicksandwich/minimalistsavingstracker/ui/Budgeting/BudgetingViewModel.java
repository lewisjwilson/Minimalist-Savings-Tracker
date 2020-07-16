package com.thicksandwich.minimalistsavingstracker.ui.Budgeting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BudgetingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BudgetingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the savings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}