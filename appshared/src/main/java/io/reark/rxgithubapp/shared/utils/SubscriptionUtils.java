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

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public final class SubscriptionUtils {

    private SubscriptionUtils() {
    }

    @NonNull
    public static Disposable subscribeTextViewText(@NonNull final Observable<String> observable,
                                                   @NonNull final TextView textView) {
        return subscribeTextViewText(observable, textView, AndroidSchedulers.mainThread());
    }

    @NonNull
    public static Disposable subscribeTextViewText(@NonNull final Observable<String> observable,
                                                   @NonNull final TextView textView,
                                                   @NonNull final Scheduler scheduler) {
        checkNotNull(observable);
        checkNotNull(textView);
        checkNotNull(scheduler);

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
