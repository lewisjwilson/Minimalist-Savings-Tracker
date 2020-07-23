package com.thicksandwich.minimalistsavingstracker.ui.Stats;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.R;

import java.util.ArrayList;

public class StatsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        StatsViewModel slideshowViewModel = ViewModelProviders.of(this).get(StatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //import database
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());

        //setup barchart
        HorizontalBarChart barChart = root.findViewById(R.id.barChart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barChart.getDescription().setEnabled(false);
        barChart.setExtraOffsets(-5, 10, 25, 10); //graph padding

        //get data to display from DBHelper class
        Cursor data = myDB.getSummedData(); //get summmed data from the db
        int dataCount = data.getCount(); //count the number of entries obtained
        ArrayList labels = new ArrayList();

        //adding data from db to arraylists
        for(int i=0; i<dataCount; i++){
            data.moveToNext();
            barEntries.add(new BarEntry(i, data.getFloat(1))); //add values to arraylist
            labels.add(data.getString(0)); //add labels to arraylist
        }


        barChart.animateY(700, Easing.EaseInCubic); //opening animation

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setDrawValues(true); //show the values on/by the bars
        barDataSet.setColor(Color.parseColor("#11f0d9")); //bar colour

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setValueTextColor(Color.BLACK);

        XAxis rightAxis = barChart.getXAxis();
        rightAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //move axis labels to the left
        rightAxis.setLabelCount(dataCount); //set number of bars as the number of rows from the db from earlier
        rightAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); //set labels as xaxis labels
        rightAxis.setDrawGridLines(false); //remove xaxis gridline

        barChart.setPinchZoom(false); //prevent zooming
        barChart.setTouchEnabled(false); //prevent touch activities
        barChart.setDrawValueAboveBar(true); //show values above the bars
        barChart.getAxisLeft().setAxisMinimum(0f); //prevent padding between labels and bars
        barChart.getLegend().setEnabled(false); //hide legend view
        barChart.getAxisRight().setEnabled(false); //hide bottom scale
        barChart.setData(barData); //tie data to barchart

        return root;
    }
}