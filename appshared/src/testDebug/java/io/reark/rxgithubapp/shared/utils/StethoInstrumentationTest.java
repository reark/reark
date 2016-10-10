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

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
