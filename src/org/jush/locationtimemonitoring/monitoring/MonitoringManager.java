/**
   Copyright: 2011 Ramon Sadornil (jush)

   This file is part of LocationTimeMonitoring.

   LocationTimeMonitoring is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   LocationTimeMonitoring is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with LocationTimeMonitoring; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jush.locationtimemonitoring.monitoring;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.jush.locationtimemonitoring.R;
import org.jush.locationtimemonitoring.monitoring.MonitoringUpdate.Flag;
import org.jush.locationtimemonitoring.util.LocalLog;

public class MonitoringManager {
    /**
     * How often the wifi is checked in minutes by default
     */
    public static final int DEFAULT_UPDATE_INTERVAL = 5;

    /**
     * Unique string used to broadcast intents to the monitoring update.
     */
    public static String MONITORING_UPDATE = MonitoringUpdate.class.getPackage().getName()
            + ".MONITORING_UPDATE";

    private static MonitoringManager singleton;

    private Context ctx;

    /**
     * This class is singleton so the constructor is hidden
     * 
     * @param applicationContext the unique application context that will be
     *            used to broadcast intents and retrieve system services.
     */
    private MonitoringManager(Context applicationContext) {
        LocalLog.debug("New MonitoringManager instance created!");
        this.ctx = applicationContext.getApplicationContext();
    }

    public static MonitoringManager getInstance(Context applicationContext) {
        if (singleton == null) {
            singleton = new MonitoringManager(applicationContext);
        }
        return singleton;
    }

    public void startMonitoring() {
        int updateInterval = getUpdateInterval();

        startMonitoring(updateInterval);
    }

    public void startMonitoring(int updateInterval) {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        // Note that unlike above, this IntentSender is configured to
        // allow itself to be sent multiple times.
        PendingIntent sender = getPendingIntent();

        // We want the alarm to go off now.
        long firstTime = SystemClock.elapsedRealtime();

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                updateInterval * 60 * 1000, sender);

        setMonitoringStatus(true);

        // Tell the user about what we did.
        LocalLog.debug("Starting monitoring Alarm every: " + updateInterval + " minute(s).");
        String startMessage = ctx.getString(R.string.monitoring_started,
                Integer.valueOf(updateInterval));
        Toast.makeText(ctx, startMessage, Toast.LENGTH_LONG).show();
    }

    public void stopMonitoring() {
        PendingIntent sender = getPendingIntent();

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        sender.cancel();

        setMonitoringStatus(false);
        // When monitoring is stop the last time variable should be cleared.
        notifyToUpdate(Flag.RESET_TIME);

        // Tell the user about what we did.
        LocalLog.debug("Stoping monitoring Alarm. ");
        Toast.makeText(ctx, R.string.monitoring_stopped, Toast.LENGTH_LONG).show();
    }

    /**
     * @param flag
     */
    private void notifyToUpdate(Flag flag) {
        Intent sender = getIntent(flag);
        ctx.sendBroadcast(sender);
    }

    public PendingIntent getPendingIntent(Flag flag) {
        Intent intent = getIntent(flag);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, PendingIntent.FLAG_CANCEL_CURRENT,
                intent, 0);
        return sender;
    }

    private Intent getIntent(Flag flag) {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.
        Intent intent = new Intent(MONITORING_UPDATE, null, ctx, MonitoringUpdate.class);
        if (flag != null)
            intent.putExtra(MONITORING_UPDATE, flag.ordinal());
        else
            intent.removeExtra(MONITORING_UPDATE);
        return intent;
    }

    private PendingIntent getPendingIntent() {
        return getPendingIntent(null);
    }

    public int getUpdateInterval() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return Integer.valueOf(prefs.getString(ctx.getString(R.string.updateIntervalID),
                DEFAULT_UPDATE_INTERVAL + ""));
    }

    private void setMonitoringStatus(boolean newStatus) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(ctx.getString(R.string.monitoringStatus), newStatus);
        prefsEditor.commit();
    }

    public boolean isMonitoring() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.monitoringStatus), false);
    }

    /**
     * 
     */
    public void resetCounter() {
        notifyToUpdate(Flag.CLEAR_COUNTERS);
    }

    /**
     * 
     */
    public void disconnecting() {
        notifyToUpdate(Flag.FORCE_UPDATE_LAST);
    }
}
