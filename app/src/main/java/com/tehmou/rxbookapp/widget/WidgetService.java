package com.tehmou.rxbookapp.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.data.DataLayer;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ttuo on 26/03/15.
 */
public class WidgetService extends Service {
    private static final String TAG = WidgetService.class.getSimpleName();

    @Inject
    DataLayer dataLayer;

    public WidgetService() {
        RxBookApp.getInstance().getGraph().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int appWidgetId = intent.getIntExtra("id", 1);
        Log.d(TAG, "onStartCommand(" + appWidgetId + ")");
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.widget_layout_title, "Loading repository..");
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        dataLayer.getGitHubRepository(15491874)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(repository -> {
                    remoteViews.setTextViewText(R.id.widget_layout_title, repository.getName());
                    remoteViews.setTextViewText(R.id.widget_layout_stargazers,
                            "stars: " + repository.getStargazersCount());
                    remoteViews.setTextViewText(R.id.widget_layout_forks,
                            "watching: " + repository.getForksCount());
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                });

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
