/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.advanced.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.R;
import io.reark.rxgithubapp.advanced.RxGitHubApp;
import io.reark.rxgithubapp.shared.data.DataFunctions;
import io.reark.rxgithubapp.shared.pojo.UserSettings;

public class WidgetService extends Service {
    private static final String TAG = WidgetService.class.getSimpleName();

    @Inject
    DataFunctions.GetUserSettings getUserSettings;

    @Inject
    DataFunctions.FetchAndGetGitHubRepository fetchAndGetGitHubRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public WidgetService() {
        RxGitHubApp.getInstance().getGraph().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.hasExtra("widgetId")) {
            final int appWidgetId = intent.getIntExtra("widgetId", 0);
            Log.d(TAG, "onStartCommand(" + appWidgetId + ")");
            updateWidget(appWidgetId);
        } else {
            Log.e(TAG, "onStartCommand(<no widgetId>)");
        }

        return START_NOT_STICKY;
    }

    private void updateWidget(final int widgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.widget_layout_title, "Loading repository..");
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        clearDisposable();

        compositeDisposable.add(
                getUserSettings.call()
                        .map(UserSettings::getSelectedRepositoryId)
                        .doOnNext(repositoryId -> Log.d(TAG, "Changed repository to " + repositoryId))
                        .switchMap(fetchAndGetGitHubRepository::call)
                        .filter(DataStreamNotification::isOnNext)
                        .map(DataStreamNotification::getValue)
                        .subscribeOn(Schedulers.io())
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
                        }));
    }

    private void clearDisposable() {
        compositeDisposable.clear();
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
