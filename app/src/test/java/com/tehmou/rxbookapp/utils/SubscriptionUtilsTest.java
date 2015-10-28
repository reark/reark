package com.tehmou.rxbookapp.utils;

import android.graphics.Color;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                .toReturn(Schedulers.immediate());
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