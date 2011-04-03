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

package com.gotdns.jush.locationtimemonitoring.monitoring;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.gotdns.jush.locationtimemonitoring.R;
import com.gotdns.jush.locationtimemonitoring.util.LocalLog;

public class MonitoringManager {
    /**
     * How often the wifi is checked in minutes
     */
    public static final int DEFAULT_UPDATE_INTERVAL = 5;

    public static String MONITORING_UPDATE = MonitoringUpdate.class.getPackage().getName()
            + ".MONITORING_UPDATE";

    public static void startMonitoring(Context ctx) {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        // Note that unlike above, this IntentSender is configured to
        // allow itself to be sent multiple times.
        Intent intent = new Intent(MONITORING_UPDATE, null, ctx, MonitoringUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();

        int updateInterval = getUpdateInterval(ctx);
        firstTime += updateInterval * 60 * 1000;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                updateInterval * 60 * 1000, sender);

        // Tell the user about what we did.
        LocalLog.debug("Starting monitoring Alarm every: " + updateInterval + " minute(s).");
        String startMessage = ctx.getString(R.string.monitoring_started,
                Integer.valueOf(updateInterval));
        Toast.makeText(ctx, startMessage, Toast.LENGTH_LONG).show();
    }

    public static void stopMonitoring(Context ctx) {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.
        Intent intent = new Intent(MONITORING_UPDATE, null, ctx, MonitoringUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);

        // Tell the user about what we did.
        LocalLog.debug("Stoping monitoring Alarm. ");
        Toast.makeText(ctx, R.string.monitoring_stopped, Toast.LENGTH_LONG).show();
    }

    public static int getUpdateInterval(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return Integer.valueOf(prefs.getString(ctx.getString(R.string.updateIntervalID),
                DEFAULT_UPDATE_INTERVAL + ""));
    }
}
