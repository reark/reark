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

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reark.reark.data.stores.mock.SimpleMockContentProvider;
import io.reark.reark.data.stores.mock.SimpleMockStore;
import io.reark.reark.data.stores.mock.SimpleMockStoreCore;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@RunWith(AndroidJUnit4.class)
public class ContentProviderStoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private SimpleMockStore store;
    private SimpleMockStoreCore core;

    public ContentProviderStoreTest() {
        super(SimpleMockContentProvider.class, SimpleMockStoreCore.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void getOnce_WithId_WithData_ReturnsData_AndCompletes() {
        new ArrangeBuilder().withTestData();

        store.getOnce(SimpleMockStore.getIdFor("parsnip"))
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue("parsnip");
    }

    @Test
    public void getOnce_WithNoId_WithData_ReturnsAllValues_AndCompletes() {
        new ArrangeBuilder().withTestData();

        store.getOnce()
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(asList("parsnip", "lettuce", "spinach"));
    }

    @Test
    public void getOnce_WithId_WithNoData_ReturnsNoneValue_AndCompletes() {
        new ArrangeBuilder();

        store.getOnce(SimpleMockStore.getIdFor("bacon"))
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(SimpleMockStore.NONE);
    }

    @Test
    public void getOnce_WithNoId_WithNoData_ReturnsEmptyList_AndCompletes() {
        new ArrangeBuilder();

        store.getOnce()
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValue(emptyList());
    }

    @Test
    public void getOnceAndStream_WithId_WithData_ReturnsData_AndDoesNotComplete() {
        new ArrangeBuilder().withTestData();

        store.getOnceAndStream(SimpleMockStore.getIdFor("spinach"))
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue("spinach");
    }

    @Test
    public void getOnceAndStream_WithId_WithNoData_ReturnsNoneValue_AndDoesNotComplete() {
        new ArrangeBuilder().withTestData();

        store.getOnceAndStream(SimpleMockStore.getIdFor("bacon"))
                .test()
                .awaitDone(50, TimeUnit.MILLISECONDS)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(SimpleMockStore.NONE);
    }

    private class ArrangeBuilder {

        ArrangeBuilder() {
            core = new SimpleMockStoreCore(getMockContentResolver());
            store = new SimpleMockStore(core);
        }

        ArrangeBuilder withTestData() {
            Consumer<String> insert = value ->
                    getProvider().insert(
                            core.getUriForId(SimpleMockStore.getIdFor(value)),
                            core.getContentValuesForItem(value)
                    );

            // Prepare the mock content provider with values
            try {
                insert.accept("parsnip");
                insert.accept("lettuce");
                insert.accept("spinach");
            } catch (Exception ignored) {
            }

            return this;
        }
    }

}
