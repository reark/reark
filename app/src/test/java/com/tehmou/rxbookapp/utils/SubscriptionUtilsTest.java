package com.tehmou.rxbookapp.utils;

import org.junit.Test;

import android.graphics.Color;
import android.widget.TextView;

import rx.Observable;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static rx.schedulers.Schedulers.immediate;

public class SubscriptionUtilsTest {

    @Test
    public void testTextViewIsUpdatedWhenValueComes() {
        TextView textView = mock(TextView.class);
        Observable<String> observable = Observable.just("String");

        // Using immediate scheduler as loopers are not mocked
        SubscriptionUtils.subscribeTextViewText(observable, textView, immediate());

        verify(textView).setText(eq("String"));
    }

    @Test
    public void testTextViewIsUpdatedWithErrorMessageOnError() {
        TextView textView = mock(TextView.class);
        Observable<String> observable = Observable.error(new NullPointerException());

        // Using immediate scheduler as loopers are not mockedError
        SubscriptionUtils.subscribeTextViewText(observable, textView, immediate());

        verify(textView).setText(contains("NullPointerException"));
        verify(textView).setBackgroundColor(eq(Color.RED));
    }

}