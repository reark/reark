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
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AndroidSchedulers.class)
public class SubscriptionUtilsTest {

    @Before
    public void setUp() {
        // Mocking AndroidSchedulers.mainThread() as loopers are not mocked by android unit tests
        PowerMockito.stub(PowerMockito.method(AndroidSchedulers.class, "mainThread"))
                .toReturn(Schedulers.trampoline());
    }

    @Test
    public void testTextViewIsUpdatedWhenValueComes() {
        TextView textView = mock(TextView.class);
        Observable<String> observable = Observable.just("String");

        SubscriptionUtils.subscribeTextViewText(observable, textView);

        verify(textView).setText(eq("String"));
    }

    @Test
    public void testTextViewIsUpdatedWithErrorMessageOnError() {
        TextView textView = mock(TextView.class);
        Observable<String> observable = Observable.error(new NullPointerException());

        SubscriptionUtils.subscribeTextViewText(observable, textView);

        verify(textView).setText(contains("NullPointerException"));
        verify(textView).setBackgroundColor(eq(Color.RED));
    }

}