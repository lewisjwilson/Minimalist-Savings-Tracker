package com.thicksandwich;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application { //used to get context/prefs in static environments

    public static final String SHARED_PREFS = "sharedPrefs";
    public static Context context;
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;

    @Override
    public void onCreate() {  //context/sharedprefs/editor defined at the application level
        super.onCreate();
        context = this;
        mPrefs = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

}
