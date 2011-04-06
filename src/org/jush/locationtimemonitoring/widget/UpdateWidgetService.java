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

package org.jush.locationtimemonitoring.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import org.jush.locationtimemonitoring.R;

public class UpdateWidgetService extends Service {
    
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
