package com.thicksandwich.minimalistsavingstracker.backend;

import android.util.Log;

import com.thicksandwich.MyApplication;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static android.content.ContentValues.TAG;

//This class takes a integer and formats it according to the currency and locale stored in SharedPreferences
public class CurrencyFormat {

    public static NumberFormat cf;
    public static Currency currency;
    public static String currency_symbol;
    public static boolean decimal_currency;

    //initialise values for SharedPreferences
    public static final String COUNTRY = "country";

    public CurrencyFormat(){
        //User currency based on locale
        Locale mLocale = loadLocale(); //Load Locale from sharedpreferences
        currency = Currency.getInstance(mLocale);
        currency_symbol = currency.getSymbol();
        Log.d(TAG, "CurrencyFormatter: " + mLocale + " " + currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(mLocale);
        numberFormat.setCurrency(currency); //Set currency format as per users selected currency of choice
        cf = numberFormat;
    }

    public Locale loadLocale(){

      String country = MyApplication.mPrefs.getString(COUNTRY, "NO COUNTRY SET");
      Locale locale;

      switch (country){
          case "Great Britain":
              locale = Locale.UK;
              decimal_currency = true;
              break;
          case "Japan":
              locale = Locale.JAPAN;
              decimal_currency = false;
              break;
          case "United States":
              locale = Locale.US;
              decimal_currency = true;
              break;
          default:
              locale = Locale.getDefault();
              decimal_currency = true;
              break;
      }

      return locale;
    }
}
