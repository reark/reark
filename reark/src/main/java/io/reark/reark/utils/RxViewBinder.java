package io.reark.reark.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 19/08/15.
 */
public abstract class RxViewBinder {

    @Nullable
    private CompositeSubscription compositeSubscription;

    public void bind() {
        unbind();
        compositeSubscription = new CompositeSubscription();
        bindInternal(compositeSubscription);
    }

    public void unbind() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
            compositeSubscription = null;
        }
    }

    protected abstract void bindInternal(@NonNull final CompositeSubscription compositeSubscription);
}
