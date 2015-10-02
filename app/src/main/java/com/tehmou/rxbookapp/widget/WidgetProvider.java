package com.tehmou.rxbookapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ttuo on 26/03/15.
 */
public class WidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent intentService = new Intent(context, WidgetService.class);
            intentService.setAction("refresh");
            intentService.putExtra("widgetId", appWidgetId);
            context.startService(intentService);
        }
    }
}
