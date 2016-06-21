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
package io.reark.rxgithubapp.advanced;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Arrays;

import javax.inject.Inject;

import io.reark.reark.utils.Log;
import io.reark.rxgithubapp.advanced.injections.Graph;
import io.reark.rxgithubapp.shared.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.advanced.widget.WidgetProvider;

public class RxGitHubApp extends Application {
    private static final String TAG = RxGitHubApp.class.getSimpleName();

    private static RxGitHubApp instance;

    private Graph mGraph;

    @Inject
    ApplicationInstrumentation instrumentation;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mGraph = Graph.Initializer.init(this);
        getGraph().inject(this);

        instrumentation.init();

        updateWidget();
    }

    @NonNull
    public static RxGitHubApp getInstance() {
        return instance;
    }

    @NonNull
    public Graph getGraph() {
        return mGraph;
    }

    public void updateWidget() {
        int widgetIds[] = AppWidgetManager
                .getInstance(this)
                .getAppWidgetIds(new ComponentName(this, WidgetProvider.class));

        Log.d(TAG, "updateWidget(" + Arrays.toString(widgetIds) + ")");

        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        sendBroadcast(intent);
    }
}
