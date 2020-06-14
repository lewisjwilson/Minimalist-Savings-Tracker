package com.lewiswilson.minimalistsavingstracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.lewiswilson.minimalistsavingstracker.ui.Home.HomeFragment;

public class DeleteDialog extends androidx.fragment.app.DialogFragment {
    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private Button dd_btn_delete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_dialog, container, false);

        final DatabaseHelper myDB = new DatabaseHelper(getActivity());
        dd_btn_delete = view.findViewById(R.id.dd_btn_delete);

        dd_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mArgs = getArguments();
                long databaseid = mArgs.getLong("id");
                myDB.removeItem(databaseid);
                dismiss();

            }
        });


        return view;
    }
}
