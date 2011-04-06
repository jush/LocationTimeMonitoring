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

import com.gotdns.jush.locationtimemonitoring.util.LocalLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author jush
 */
public class MonitoringActionHandler extends BroadcastReceiver {

    private MonitoringManager monitoringManager;

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (monitoringManager == null) {
            LocalLog.debug("Getting MonitoringManager instance in MonitoringUpdate");
            monitoringManager = MonitoringManager.getInstance(context.getApplicationContext());
        }
        String actionID = intent.getAction();
        LocalLog.debug("Received intent: " + actionID);
        if (actionID == null) {
            return;
        }
        if (actionID.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            handleWifiStateChanged(context);
        } else if (actionID.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // TODO: If wireless is on when boot then the update should be started?
            monitoringManager.stopMonitoring();
        } else if (actionID.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            handleNetworkStateChanged(context, intent);
        }
    }

    /**
     * @param context
     * @param intent
     */
    private void handleNetworkStateChanged(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            LocalLog.debug("Unable to fetch extra network information");
            return;
        }
        NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo == null) {
            LocalLog.debug("Unable to fetch extra network information");
        }
        LocalLog.debug("New network state: " + networkInfo.getState());
        if (networkInfo.getState() == NetworkInfo.State.DISCONNECTING) {
            LocalLog.debug("Network is disconnecting...");
            monitoringManager.disconnecting();
        } else if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            LocalLog.debug("Network is connected");
            monitoringManager.startMonitoring();
        }
    }

    private void handleWifiStateChanged(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Toast.makeText(context, "Unable to update wifi status", Toast.LENGTH_LONG);
            LocalLog.debug("Unable to update wifi status");
        }
        switch (wifiManager.getWifiState()) {
            case WifiManager.WIFI_STATE_ENABLED:
                // Nothing to do, monitoring will start once the device is connected
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                monitoringManager.disconnecting();
                monitoringManager.stopMonitoring();
                break;
        }
    }

}
