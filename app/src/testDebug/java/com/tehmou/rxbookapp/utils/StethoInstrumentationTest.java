package com.tehmou.rxbookapp.utils;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Pawel Polanski on 7/20/15.
 */
public class StethoInstrumentationTest {

    private StethoInstrumentation instrumentation;

    @Mock
    Interceptor interceptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        instrumentation = spy(new StethoInstrumentation(mock(Context.class), interceptor));
    }

    @Test
    public void testInitDoesNotThrow() {
        doNothing().when(instrumentation).initStetho();

        instrumentation.init();

        verify(instrumentation).initStetho();
    }

    @Test
    public void testDecorateNetwork() {
        @SuppressWarnings("unchecked")
        List<Interceptor> interceptors = mock(List.class);
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        when(okHttpClient.networkInterceptors()).thenReturn(interceptors);

        instrumentation.decorateNetwork(okHttpClient);

        verify(instrumentation).addInterceptor(eq(okHttpClient), eq(interceptor));
    }
}
