package com.tehmou.rxbookapp.viewmodels;

import android.util.Log;

import com.tehmou.rxbookapp.data.DataLayer;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 06/04/15.
 */
abstract public class AbstractViewModel {
    private static final String TAG = AbstractViewModel.class.getSimpleName();
    private CompositeSubscription compositeSubscription;

    @Inject
    DataLayer dataLayer;

    final public void subscribeToDataStore() {
        Log.v(TAG, "subscribeToDataStore");
        unsubscribeFromDataStore();
        compositeSubscription = new CompositeSubscription();
        subscribeToDataStoreInternal(compositeSubscription);
    }

    abstract void subscribeToDataStoreInternal(CompositeSubscription compositeSubscription);

    protected void unsubscribeFromDataStore() {
        Log.v(TAG, "unsubscribeToDataStore");
        if (compositeSubscription != null) {
            compositeSubscription.clear();
            compositeSubscription = null;
        }
    }
}
