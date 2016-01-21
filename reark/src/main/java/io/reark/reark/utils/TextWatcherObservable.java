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
package io.reark.reark.utils;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class TextWatcherObservable {
    static public Observable<String> create(@NonNull EditText editText) {
        Preconditions.checkNotNull(editText, "Edit Text cannot be null.");

        return Observable.create(new OnSubscribe(editText));
    }

    static class OnSubscribe implements TextWatcher, Observable.OnSubscribe<String> {
        final private Subject<String, String> subject = BehaviorSubject.create();

        private OnSubscribe(@NonNull EditText editText) {
            editText.addTextChangedListener(this);
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            subject.subscribe(subscriber);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            subject.onNext(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
