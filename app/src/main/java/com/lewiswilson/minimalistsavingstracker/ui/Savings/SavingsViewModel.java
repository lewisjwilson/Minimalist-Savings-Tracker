package com.lewiswilson.minimalistsavingstracker.ui.Savings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SavingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the savings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}