package com.lewiswilson.minimalistsavingstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DeleteDialog extends androidx.fragment.app.DialogFragment {

    private TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_dialog, container, false);

        tv = view.findViewById(R.id.textView);

        Bundle mArgs = getArguments();
        int rvPosition = mArgs.getInt("position");
        tv.setText("Recycler Position: " + String.valueOf(rvPosition));



        return view;
    }
}
