package com.thicksandwich.minimalistsavingstracker.menutabs.Stats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.thicksandwich.minimalistsavingstracker.DatabaseHelper;
import com.thicksandwich.minimalistsavingstracker.R;
import com.thicksandwich.minimalistsavingstracker.backend.CurrencyFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class StatsFragment extends Fragment {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FIRST_TIME_STATS = "first_time_stats";
    private SharedPreferences sharedPreferences;

    public static int yearint;
    public static int monthint;

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

        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //check for first time run
        if(sharedPreferences.getBoolean(FIRST_TIME_STATS, true)){ //if first time
            Log.d(TAG, "HomeFragment: First time run");
            firstTimeHints(getActivity());
            sharedPreferences.edit().putBoolean(FIRST_TIME_STATS, false).apply();
        }

        //import database
        final DatabaseHelper myDB = new DatabaseHelper(getActivity());

        //setup barchart
        final HorizontalBarChart barChart = root.findViewById(R.id.barChart);

        //get year and month
        yearint = Calendar.getInstance().get(Calendar.YEAR); //current year
        monthint = Calendar.getInstance().get(Calendar.MONTH) + 1; //current month
        final String yearstr = String.valueOf(yearint);
        final String monthstr = String.format("%02d", monthint); //padding with leading 0 where needed
        //change textview for year and month
        final TextView txt_year_month = root.findViewById(R.id.txt_statsyearmth);
        txt_year_month.setText(yearstr + "/" + monthstr);

        final TextView txt_YLabel = root.findViewById(R.id.txt_YLabel);
        txt_YLabel.setText("Amount Spent (" + CurrencyFormat.currency_symbol + ")");

        //get data to display from DBHelper class
        Cursor data = myDB.getSummedData(yearstr, monthstr); //get summmed data from the db
        RefreshGraph(barChart, data);

        ImageButton prev_button = root.findViewById(R.id.btn_budmthprev);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monthint>1){ //changing the year and month values
                    monthint--;
                } else {
                    yearint--;
                    monthint = 12;
                }
                String newyearstr = String.valueOf(yearint);
                String newmonthstr = String.format("%02d", monthint);
                txt_year_month.setText(newyearstr + "/" + newmonthstr);

                Cursor data = myDB.getSummedData(newyearstr, newmonthstr);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                barChart.refreshDrawableState();
                RefreshGraph(barChart, data);

            }
        });

        ImageButton next_button = root.findViewById(R.id.btn_statsmthnext);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monthint<12){ //changing the year and month values
                    monthint++;
                } else {
                    yearint++;
                    monthint = 1;
                }
                String newyearstr = String.valueOf(yearint);
                String newmonthstr = String.format("%02d", monthint);
                txt_year_month.setText(newyearstr + "/" + newmonthstr);

                Cursor data = myDB.getSummedData(newyearstr, newmonthstr);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                barChart.refreshDrawableState();
                RefreshGraph(barChart, data);


            }
        });

        return root;
    }

    public void RefreshGraph(HorizontalBarChart barChart, Cursor data){

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int dataCount = data.getCount(); //count the number of entries obtained
        if(dataCount==0){
            barChart.clear();
        } else {

            ArrayList labels = new ArrayList();

            //adding data from db to arraylists
            for (int i = 0; i < dataCount; i++) {
                data.moveToNext();
                barEntries.add(new BarEntry(i, Float.parseFloat(moneyFormat(data.getLong(1))))); //add values to arraylist
                labels.add(data.getString(0)); //add labels to arraylist
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, CurrencyFormat.currency_symbol);
            barDataSet.setDrawValues(true); //show the values on/by the bars
            barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent)); //bar colour

            BarData barData = new BarData(barDataSet);
            barData.setValueTextSize(10f);
            barData.setValueTextColor(Color.BLACK);

            XAxis rightAxis = barChart.getXAxis();
            rightAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //move axis labels to the left
            rightAxis.setLabelCount(dataCount); //set number of bars as the number of rows from the db from earlier
            rightAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); //set labels as xaxis labels
            rightAxis.setDrawGridLines(false); //remove xaxis gridline

            barChart.animateY(700, Easing.EaseInCubic); //opening animation
            barChart.setPinchZoom(false); //prevent zooming
            barChart.setTouchEnabled(false); //prevent touch activities
            barChart.setDrawValueAboveBar(true); //show values above the bars
            barChart.getAxisLeft().setAxisMinimum(0f); //prevent padding between labels and bars
            barChart.getLegend().setEnabled(false); //hide legend view
            barChart.getAxisRight().setEnabled(false); //hide bottom scale
            barChart.getDescription().setEnabled(false);
            barChart.setExtraOffsets(-5, 10, 25, 10); //graph padding
            barChart.setNoDataText("No data");
            barChart.setNoDataTextColor(R.color.black);
            Paint paint = barChart.getPaint(Chart.PAINT_INFO);
            paint.setTextSize(60);
            barChart.setData(barData); //tie data to barchart
        }

    }
    public String moneyFormat(long i){
        BigDecimal output;

        if(CurrencyFormat.decimal_currency) {
            output = new BigDecimal(BigInteger.valueOf(i), 2);
        } else {
            output = new BigDecimal(BigInteger.valueOf(i));
        }

        //special formatting as barentries do not accept symbols. Remove currency sign and and commas
        return CurrencyFormat.cf.format(output).substring(1).replaceAll("[,]",""); //format currency

    }

    public void firstTimeHints(Activity activity){

        final FancyShowCaseView intro = new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(this.getContext(), R.color.colorHintBg))
                .title("This is the Stats screen. Here you can see your spending habits.")
                .titleStyle(R.style.HintsStyle, Gravity.CENTER)
                .fitSystemWindows(true)
                .enableAutoTextPosition()
                .build();

        FancyShowCaseQueue mQueue = new FancyShowCaseQueue()
                .add(intro);

        mQueue.show();
    }
}