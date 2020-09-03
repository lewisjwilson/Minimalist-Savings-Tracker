package com.thicksandwich.minimalistsavingstracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.thicksandwich.MyApplication;
import com.thicksandwich.minimalistsavingstracker.login.ChangePin;
import com.thicksandwich.minimalistsavingstracker.login.ChangeTwoFactor;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //initialise values for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NOTIFICATIONS = "notifications";
    private static final String TAG = "";
    private SharedPreferences sharedPreferences;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_budgeting, R.id.nav_stats)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //start of own code....above is menubar layout stuff!
        //Get SharedPreferences---------------------------------------------------------------------
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //display the menu
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_credits: //if credits is selected
                startActivity(new Intent(this, Credits.class));
                return true;
            case R.id.action_changepin:
                startActivity(new Intent(this, ChangePin.class));
                return true;
            case R.id.action_changetwofactor:
                startActivity(new Intent(this, ChangeTwoFactor.class));
                return true;
            case R.id.action_notifications:
                Boolean enabled = sharedPreferences.getBoolean(NOTIFICATIONS, false); // if not created, value is false
                if(enabled){ //
                    enableDisableNotifications(true);
                    enabled = false;
                } else {
                    enableDisableNotifications(false);
                    enabled = true;
                }

                MyApplication.mEditor.putBoolean(NOTIFICATIONS, enabled); //set notifications status in sharedprefs
                MyApplication.mEditor.commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableDisableNotifications(Boolean enabled) {

        String channelID = "daily";

        //create channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Daily Notifications";
            String description = "Daily Notifications Channel";
            int importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(enabled){ //if notifications are enabled
            try {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "enableDisableNotifications: Cancelled Notifications");
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Log.d(TAG, "enableDisableNotifications: AlarmManager not cancelled ... " + e.toString());
            }
        } else { //if notifications are not enabled
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(this, "Notifications Enabled at 9pm daily", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
