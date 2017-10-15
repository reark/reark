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

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.observers.TestObserver;

import static io.reactivex.Observable.just;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class RxUtilsTest {

    @Test
    public void testToListReturnsCombinedListOfItems() {
        assertEquals(3, RxUtils.toList(new Object[]{"1", "2", "3"}).size());
    }

    @Test
    public void testToListReturnsCombinedListOfItems1() {
        TestObserver<List<String>> observer = new TestObserver<>();

        RxUtils.toObservableList(asList(just("1"), just("2"), just("1"), just("2")))
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertValue(asList("1", "2", "1", "2"));
    }
}
