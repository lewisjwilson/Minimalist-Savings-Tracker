package com.thicksandwich.minimalistsavingstracker.backend;

import android.util.Log;

import com.thicksandwich.MyApplication;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static android.content.ContentValues.TAG;

//This class takes a integer and formats it according to the currency and locale stored in SharedPreferences
public class CurrencyFormat {

    public static NumberFormat currencyFormat;
    public static Currency currency;

    //initialise values for SharedPreferences
    public static final String COUNTRY = "country";

    public CurrencyFormat(){
        //User Locale based on Currency
        Locale mLocale = loadLocale(); //Load Locale from sharedpreferences
        currency = Currency.getInstance(mLocale);
        Log.d(TAG, "CurrencyFormatter: " + mLocale + " " + currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(mLocale);
        numberFormat.setCurrency(currency); //Set currency format as per users selected currency of choice
        currencyFormat = numberFormat;
    }

    public Locale loadLocale(){

      String country = MyApplication.mPrefs.getString(COUNTRY, "NO COUNTRY SET");
      Locale locale;

      switch (country){
          case "Great Britain":
              locale = Locale.UK;
              break;
          case "Japan":
              locale = Locale.JAPAN;
              break;
          case "United States":
              locale = Locale.US;
              break;
          default:
              locale = Locale.getDefault();
              break;
      }

      return locale;
    }
}
