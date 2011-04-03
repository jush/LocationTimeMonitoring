
package com.gotdns.jush.locationtimemonitoring.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.gotdns.jush.locationtimemonitoring.util.LocalLog;

public class MainWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LocalLog.debug("Update!");
        // To prevent any ANR timeouts, we perform the update in a service
        // context.startService(new Intent(context, UpdateService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalLog.debug("onReceive");
        super.onReceive(context, intent);
    }
}
