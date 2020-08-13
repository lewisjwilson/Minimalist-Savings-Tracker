package com.thicksandwich.minimalistsavingstracker;

import java.util.Locale;

public class LocaleGetter {

    public Locale UserLocale(String currency_code){

        Locale locale;
        switch (currency_code){ //switch locale based on country
            case "GBP":
                locale = Locale.UK;
                break;
            case "JPY":
                locale = Locale.JAPAN;
                break;
            case "USD":
                locale = Locale.US;
                break;
            default:
                locale = Locale.getDefault();
                break;
        }
        return locale;
    }

}
