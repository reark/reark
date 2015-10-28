package io.reark.rxbookapp.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import io.reark.rxbookapp.R;
import io.reark.rxbookapp.RxBookApp;
import io.reark.rxbookapp.data.DataLayer;
import io.reark.rxbookapp.pojo.UserSettings;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 26/03/15.
 */
public class WidgetService extends Service {
    private static final String TAG = WidgetService.class.getSimpleName();

    @Inject
    DataLayer.GetUserSettings getUserSettings;

    @Inject
    DataLayer.FetchAndGetGitHubRepository fetchAndGetGitHubRepository;

    private CompositeSubscription subscriptions;

    public WidgetService() {
        RxBookApp.getInstance().getGraph().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("widgetId")) {
            final int appWidgetId = intent.getIntExtra("widgetId", 0);
            Log.d(TAG, "onStartCommand(" + appWidgetId + ")");
            updateWidget(appWidgetId);
        } else {
            Log.e(TAG, "onStartCommand(<no widgetId>)");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWidget(final int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.widget_layout_title, "Loading repository..");
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        clearSubscriptions();
        subscriptions.add(
                getUserSettings.call()
                        .map(UserSettings::getSelectedRepositoryId)
                        .switchMap(fetchAndGetGitHubRepository::call)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(repository -> {
                            remoteViews.setTextViewText(R.id.widget_layout_title, repository.getName());
                            remoteViews.setTextViewText(R.id.widget_layout_stargazers,
                                    "stars: " + repository.getStargazersCount());
                            remoteViews.setTextViewText(R.id.widget_layout_forks,
                                    "forks: " + repository.getForksCount());

                            AppWidgetTarget widgetTarget = new AppWidgetTarget(WidgetService.this,
                                                                    remoteViews,
                                                                    R.id.widget_avatar_image_view,
                                                                    widgetId);
                            Glide.with(WidgetService.this)
                                 .load(repository.getOwner().getAvatarUrl())
                                 .asBitmap()
                                 .fitCenter()
                                 .into(widgetTarget);
                            appWidgetManager.updateAppWidget(widgetId, remoteViews);
                        })
        );
    }

    private void clearSubscriptions() {
        if (subscriptions != null) {
            subscriptions.clear();
        }
        subscriptions = new CompositeSubscription();
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
