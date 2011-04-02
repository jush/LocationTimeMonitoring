
package com.gotdns.jush.locationtimemonitoring.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gotdns.jush.locationtimemonitoring.R;

public class MainActivity extends Activity {
    /**
     * How often the wifi is checked in minutes
     */
    public static final int DEFAULT_UPDATE_INTERVAL = 5;

    private static final String TAG = "com.gotdns.jush";

    private Toast mToast;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_options:
                Intent settingsActivity = new Intent(getBaseContext(), MainPreferences.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void monitoringBtClicked(View view) {
        CheckBox monitoringBt = (CheckBox) view;
        Log.d(TAG, "Monitoring toggle button clicked!" + monitoringBt.isChecked());
        if (monitoringBt.isChecked()) {
            monitoringBt.setText(R.string.main_stop_button);
            startMonitoring();
        } else {
            monitoringBt.setText(R.string.main_start_button);
            stopMonitoring();
        }
    }

    private void stopMonitoring() {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.
        Intent intent = new Intent(MainActivity.this, MonitoringUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
        Log.d(TAG, "Stoping monitoring Alarm. ");

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, R.string.monitoring_stopped, Toast.LENGTH_LONG);
        mToast.show();
    }

    private void startMonitoring() {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        // Note that unlike above, this IntentSender is configured to
        // allow itself to be sent multiple times.
        Intent intent = new Intent(MonitoringUpdate.MONITORING_UPDATE, null, MainActivity.this, MonitoringUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        
        int updateInterval = getUpdateInterval();
        firstTime += updateInterval * 60 * 1000;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, updateInterval * 60 * 1000,
                sender);

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        Log.d(TAG, "Starting monitoring Alarm every: " + updateInterval  + " minute(s).");
        String startMessage = getString(R.string.monitoring_started, Integer.valueOf(updateInterval));
        mToast = Toast.makeText(MainActivity.this, startMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

    private int getUpdateInterval() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.valueOf(prefs.getString(getString(R.string.updateIntervalID), DEFAULT_UPDATE_INTERVAL + ""));
    }

    public void resetBtClicked(View v) {
        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, "Counters reset!", Toast.LENGTH_LONG);
        mToast.show();
        MonitoringUpdate.resetCounter();
    }
}
