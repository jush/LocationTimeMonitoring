
package com.gotdns.jush.locationtimemonitoring;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    HashMap<String, Long> totalTimes = new HashMap<String, Long>();

    private Handler checkHandler;

    private static long CHECK_DELAY = 15000;//5 * 60 * 1000;

    Runnable checkConnected = new Runnable() {

        @Override
        public void run() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            switch (wifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_ENABLED:
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String currentSSID = wifiInfo.getSSID();
                    Log.d("Wifi", "SSID: " + currentSSID);

                    Long totalTime = totalTimes.get(currentSSID);
                    if (totalTime != null) {
                        totalTime += CHECK_DELAY / 1000;
                    } else {
                        totalTime = CHECK_DELAY / 1000;
                    }
                    totalTimes.put(currentSSID, totalTime);

                    TextView totalTimeText = (TextView) findViewById(R.id.totalTimeText);
                    totalTimeText.setText("You've been connected to '" + currentSSID + "' for "
                            + totalTime + " seconds.");
                    break;
            }
            checkHandler.postDelayed(checkConnected, CHECK_DELAY);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkHandler = new Handler();
        checkHandler.postDelayed(checkConnected, CHECK_DELAY);
    }
}
