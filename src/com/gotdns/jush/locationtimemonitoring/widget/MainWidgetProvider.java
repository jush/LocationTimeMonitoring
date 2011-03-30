
package com.gotdns.jush.locationtimemonitoring.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gotdns.jush.locationtimemonitoring.activities.UpdateService;

public class MainWidgetProvider extends AppWidgetProvider {
    // log tag
    private static final String TAG = "MainWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Update!");
        // To prevent any ANR timeouts, we perform the update in a service
        context.startService(new Intent(context, UpdateService.class));
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
    }
}
