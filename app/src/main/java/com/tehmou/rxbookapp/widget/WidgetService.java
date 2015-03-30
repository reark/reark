package com.tehmou.rxbookapp.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tehmou.rxbookapp.R;

import rx.Subscriber;

/**
 * Created by ttuo on 26/03/15.
 */
public class WidgetService extends Service {
    private static final String TAG = WidgetService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int appWidgetId = intent.getIntExtra("id", 1);
        Log.d(TAG, "onStartCommand(" + appWidgetId + ")");
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.widget_layout_text, "lolz");
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
