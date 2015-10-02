package com.tehmou.rxbookapp.utils;

import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by Pawel Polanski on 7/20/15.
 */
public class NullNetworkInstrumentationTest {

    private NullNetworkInstrumentation instrumentation;

    @Before
    public void setUp() throws Exception {
        instrumentation = new NullNetworkInstrumentation();
    }

    @Test
    public void testDecorateNetwork_DoesNotChangeTheHttpClient() {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        instrumentation.decorateNetwork(okHttpClient);

        verifyZeroInteractions(okHttpClient);
    }

}
