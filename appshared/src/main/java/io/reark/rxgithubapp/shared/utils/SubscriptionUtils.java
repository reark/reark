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
package io.reark.rxgithubapp.shared.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.TextView;

import io.reark.reark.utils.Preconditions;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SubscriptionUtils {
    private SubscriptionUtils() { }

    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView) {
        return subscribeTextViewText(observable, textView, AndroidSchedulers.mainThread());
    }

    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView,
                                                     @NonNull Scheduler scheduler) {
        Preconditions.checkNotNull(observable, "Observable cannot be null.");
        Preconditions.checkNotNull(textView, "TextView cannot be null.");
        Preconditions.checkNotNull(scheduler, "Scheduler cannot be null.");

        return observable
                .observeOn(scheduler)
                .subscribe(textView::setText,
                        error -> {
                            textView.setText(error.toString());
                            textView.setBackgroundColor(Color.RED);
                        }
                );
    }
}
