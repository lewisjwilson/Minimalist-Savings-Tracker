package com.thicksandwich.minimalistsavingstracker.menutabs.StandingOrders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StOrdViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StOrdViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("No Standing Orders have been created");
    }

    public LiveData<String> getText() {
        return mText;
    }
}