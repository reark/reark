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
package io.reark.reark.viewmodels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reark.reark.utils.Log;

public abstract class AbstractViewModel {
    private static final String TAG = AbstractViewModel.class.getSimpleName();

    @Nullable
    private CompositeDisposable compositeDisposable;

    public void subscribeToDataStore() {
        Log.v(TAG, "subscribeToDataStore");

        if (!isSubscribed()) {
            compositeDisposable = new CompositeDisposable();
            subscribeToDataStoreInternal(compositeDisposable);
        }
    }

    public void dispose() {
        Log.v(TAG, "dispose");

        if (isSubscribed()) {
            Log.e(TAG, "Disposing without calling unsubscribeFromDataStore first");

            // Unsubscribe in case we are still for some reason subscribed
            unsubscribeFromDataStore();
        }
    }

    public abstract void subscribeToDataStoreInternal(@NonNull final CompositeDisposable compositeDisposable);

    public void unsubscribeFromDataStore() {
        Log.v(TAG, "unsubscribeToDataStore");

        if (isSubscribed()) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }

    private boolean isSubscribed() {
        return compositeDisposable != null;
    }
}
