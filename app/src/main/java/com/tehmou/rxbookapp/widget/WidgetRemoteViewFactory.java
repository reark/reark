package com.tehmou.rxbookapp.widget;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tehmou.rxbookapp.R;

/**
 * Created by ttuo on 26/03/15.
 */
public class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = WidgetRemoteViewFactory.class.getSimpleName();
    final private Context context;

    public WidgetRemoteViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "getViewAt(" + position + ")");
        return new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
