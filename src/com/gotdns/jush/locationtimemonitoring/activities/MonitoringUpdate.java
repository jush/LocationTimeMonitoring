
package com.gotdns.jush.locationtimemonitoring.activities;

import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gotdns.jush.locationtimemonitoring.R;
import com.gotdns.jush.locationtimemonitoring.util.LocalLog;
import com.gotdns.jush.locationtimemonitoring.widget.MainWidgetProvider;

public class MonitoringUpdate extends BroadcastReceiver {

    public static String MONITORING_UPDATE = MonitoringUpdate.class.getPackage().getName()
            + ".MONITORING_UPDATE";

    // TODO: This should be stored in a more permanent place. Like into a
    // database
    private static HashMap<String, Long> totalTimes = new HashMap<String, Long>();

    private String lastWifiSSID;

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionID = intent.getAction();
        LocalLog.debug("Received intent: " + actionID);
        if (actionID != null && actionID.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            handleWifiStateChanged();
        } else if (actionID != null && actionID.equals(MONITORING_UPDATE)) {
            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
            updateCounters(context);
            updateWidget(context);
        }
    }

    private void handleWifiStateChanged() {
        // TODO Cancel Alarm and other stuff
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
                int updateInterval = MonitoringManager.getUpdateInterval(context);
                if (totalTime != null) {
                    totalTime += updateInterval;
                } else {
                    totalTime = (long) updateInterval;
                }

                LocalLog.debug("Updating SSID: " + lastWifiSSID + " to " + totalTime);

                totalTimes.put(lastWifiSSID, totalTime);
                break;
            default:
                LocalLog.debug("Wifi state: " + wifiManager.getWifiState());
                break;
        }
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
            int minutes = (int) (totalTime % 60);
            int hours = (int) (totalTime / 60);
            updateView.setTextViewText(R.id.widget_textview, "You've been connected to '"
                    + lastWifiSSID + "' " + hours + " hours and " + minutes + " minutes.");
        }
        return updateView;
    }

    public static void resetCounter() {
        for (String totalTimeKey : totalTimes.keySet()) {
            totalTimes.put(totalTimeKey, new Long(0));
        }
    }
}
