
package com.gotdns.jush.locationtimemonitoring.activities;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.gotdns.jush.locationtimemonitoring.R;
import com.gotdns.jush.locationtimemonitoring.widget.MainWidgetProvider;

public class UpdateService extends Service {
    
    public static int counter = 0;

    @Override
    public void onStart(Intent intent, int startId) {
        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, MainWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        RemoteViews updateViews = buildUpdateView(this);
        manager.updateAppWidget(thisWidget, updateViews);
    }
    
    RemoteViews buildUpdateView(Context context) {
     // Didn't find word of day, so show error message
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.mainwidget);
        CharSequence text = context.getText(R.string.widget_text);
        updateView.setTextViewText(R.id.widget_textview, "Moi " + counter++);
        return updateView;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
