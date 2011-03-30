
package com.gotdns.jush.locationtimemonitoring.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gotdns.jush.locationtimemonitoring.R;

public class MainActivity extends Activity {
    /**
     * How often the wifi is checked in seconds
     */
    public static final long UPDATE_INTERVAL = 5 * 60;

    private static final String TAG = "MainActivity";

    private Toast mToast;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        Intent intent = new Intent(MainActivity.this, MonitoringUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += UPDATE_INTERVAL * 1000;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, UPDATE_INTERVAL * 1000, sender);

        // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, R.string.monitoring_started, Toast.LENGTH_LONG);
        mToast.show();
    }
    
    public void resetBtClicked(View v){
     // Tell the user about what we did.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, "Counters reset!", Toast.LENGTH_LONG);
        mToast.show();
        MonitoringUpdate.resetCounter();
    }
}
