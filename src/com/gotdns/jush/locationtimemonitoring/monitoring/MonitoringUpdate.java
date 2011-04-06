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

import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gotdns.jush.locationtimemonitoring.R;
import com.gotdns.jush.locationtimemonitoring.util.LocalLog;
import com.gotdns.jush.locationtimemonitoring.widget.MainWidgetProvider;

public class MonitoringUpdate extends BroadcastReceiver {
    public enum Flag {
        NOTHING, RESET_TIME, CLEAR_COUNTERS
    }

    private MonitoringManager monitoringManager;

    // TODO: This should be stored in a more permanent place. Like into a
    // database
    private static HashMap<String, Long> totalTimes = new HashMap<String, Long>();

    private String lastWifiSSID;

    private static long lastTimeUpdated = -1;

    public MonitoringUpdate() {
        LocalLog.debug("Monitoring update created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (monitoringManager == null) {
            LocalLog.debug("Getting MonitoringManager instance in MonitoringUpdate");
            monitoringManager = MonitoringManager.getInstance(context.getApplicationContext());
        }
        String actionID = intent.getAction();
        LocalLog.debug("Received intent: " + actionID);
        if (actionID != null && actionID.equals(MonitoringManager.MONITORING_UPDATE)) {
            if (handleFlag(intent)) {
                updateCounters(context);
                updateWidget(context);
            } else {
                LocalLog.debug("Update has been cancelled");
            }
        }
    }

    /**
     * @param intent
     * @return false if update should be cancelled.
     */
    private boolean handleFlag(Intent intent) {
        if (intent.hasExtra(MonitoringManager.MONITORING_UPDATE)) {
            int flag = intent.getIntExtra(MonitoringManager.MONITORING_UPDATE,
                    Flag.NOTHING.ordinal());
            switch (Flag.values()[flag]) {
                case CLEAR_COUNTERS:
                    clearCounters();
                    // Also resets the time so there's no break
                case RESET_TIME:
                    lastTimeUpdated = -1;
                    return false;
            }
        }
        return true;
    }

    /**
     * 
     */
    private void clearCounters() {
        LocalLog.debug("Counters before clearing:");
        for (String ssid : totalTimes.keySet()){
            LocalLog.debug("\t" + ssid + ": " + totalTimes.get(ssid));
        }
        totalTimes.clear();
    }

    private void updateCounters(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Toast.makeText(context, "Unable to update wifi status", Toast.LENGTH_LONG);
            LocalLog.debug("Unable to update wifi status");
        }
        switch (wifiManager.getWifiState()) {
            case WifiManager.WIFI_STATE_ENABLED:
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                lastWifiSSID = wifiInfo.getSSID();

                Long totalTime = totalTimes.get(lastWifiSSID);
                long elapsedTime = getElapsedTime();
                if (totalTime != null) {
                    totalTime += elapsedTime;
                } else {
                    totalTime = elapsedTime;
                }

                LocalLog.debug("Updating SSID: " + lastWifiSSID + " to " + totalTime);

                totalTimes.put(lastWifiSSID, totalTime);
                break;
            default:
                LocalLog.debug("Wifi state: " + wifiManager.getWifiState());
                break;
        }
    }

    /**
     * @return
     */
    private long getElapsedTime() {
        if (lastTimeUpdated < 0) {
            // The first time the update is called
            lastTimeUpdated = SystemClock.elapsedRealtime();
            LocalLog.debug("Last time updated was zero, now is : " + lastTimeUpdated);
            return 0;
        }
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - lastTimeUpdated;
        LocalLog.debug("Elapsed time is: " + elapsedTime);
        lastTimeUpdated = currentTime;
        LocalLog.debug("Last time updated now is : " + lastTimeUpdated);
        return elapsedTime / 1000;
    }

    private void updateWidget(Context context) {
        ComponentName thisWidget = new ComponentName(context, MainWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews updateViews = buildUpdateView(context);
        manager.updateAppWidget(thisWidget, updateViews);
    }

    private RemoteViews buildUpdateView(Context context) {
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.mainwidget);
        if (lastWifiSSID == null) {
            updateView.setTextViewText(R.id.widget_textview, "Device disconnected.");
        } else {
            long totalTime = totalTimes.get(lastWifiSSID);
            int seconds = (int) totalTime % 60;
            int minutes = (int) ((totalTime / 60) % 60);
            int hours = (int) (totalTime / 3600);
            updateView.setTextViewText(R.id.widget_textview, "You've been connected to '"
                    + lastWifiSSID + "' " + hours + " hours, " + minutes + " minutes and "
                    + seconds + " seconds.");
        }
        return updateView;
    }
}
