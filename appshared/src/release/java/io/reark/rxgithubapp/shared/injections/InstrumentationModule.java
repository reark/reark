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
package io.reark.rxgithubapp.shared.injections;

import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.rxgithubapp.shared.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.shared.utils.Instrumentation;
import io.reark.rxgithubapp.shared.utils.NullApplicationInstrumentation;
import io.reark.rxgithubapp.shared.utils.NullInstrumentation;
import okhttp3.Interceptor;

@Module
public class InstrumentationModule {

    @Provides
    @Singleton
    public ApplicationInstrumentation provideApplicationInstrumentation() {
        return new NullApplicationInstrumentation();
    }

    @Provides
    @Singleton
    @Named("networkInstrumentation")
    public Instrumentation provideNetworkInstrumentation() {
        return new NullInstrumentation();
    }

    @Provides
    @Singleton
    @Named("networkInterceptors")
    public List<Interceptor> provideNetworkInterceptors() {
        return Collections.emptyList();
    }
}
