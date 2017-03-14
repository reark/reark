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
package io.reark.reark.data.stores;

import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

@RunWith(AndroidJUnit4.class)
public class ContentProviderStoreCoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private SimpleMockStoreCore core;

    public ContentProviderStoreCoreTest() {
        super(SimpleMockContentProvider.class, SimpleMockStoreCore.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        core = new SimpleMockStoreCore(getMockContentResolver());

        Action1<String> insert = value ->
                getProvider().insert(
                        core.getUriForId(SimpleMockStore.getIdFor(value)),
                        core.getContentValuesForItem(value)
                );

        // Prepare the mock content provider with values
        insert.call("parsnip");
        insert.call("lettuce");
        insert.call("spinach");
    }

    @Test
    public void getCached_WithId_ReturnsData_AndCompletes() {
        List<String> expected = Collections.singletonList("parsnip");

        core.getCached(SimpleMockStore.getIdFor("parsnip"))
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertReceivedOnNext(expected);
    }

    @Test
    public void getCached_WithNoId_ReturnsAllData_AndCompletes() {
        List<List<String>> expected = Collections.singletonList(Arrays.asList("parsnip", "lettuce", "spinach"));

        core.getCached()
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertReceivedOnNext(expected);
    }

}
