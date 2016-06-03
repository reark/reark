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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LockObjectDealerTest {

    @Test
    public void testSameKeyGivesSameLockObject() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        Object obj1 = locker.getLock("foo");
        Object obj2 = locker.getLock("foo");

        assertTrue(obj1 == obj2);
    }

    @Test
    public void testDifferentKeyGivesDifferentLockObjects() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        locker.createLock("bar");
        Object obj1 = locker.getLock("foo");
        Object obj2 = locker.getLock("bar");

        assertFalse(obj1 == obj2);
    }

    @Test
    public void testNewLockObjectAfterFreeing() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        Object obj1 = locker.getLock("foo");
        locker.freeLock("foo");
        locker.createLock("foo");
        Object obj2 = locker.getLock("foo");

        assertFalse(obj1 == obj2);
    }

    @Test
    public void testCreatesNeedEqualNumberOfFrees() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        locker.createLock("foo");
        Object obj1 = locker.getLock("foo");
        locker.freeLock("foo");
        Object obj2 = locker.getLock("foo");
        locker.freeLock("foo");
        locker.createLock("foo");
        Object obj3 = locker.getLock("foo");

        assertTrue(obj1 == obj2);
        assertFalse(obj2 == obj3);
    }

    @Test(expected=NullPointerException.class)
    public void testCantGetBeforeCreating() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.getLock("foo");
    }

    @Test(expected=NullPointerException.class)
    public void testCantGetAfterFreeing() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        locker.freeLock("foo");
        locker.getLock("foo");
    }

    @Test(expected=NullPointerException.class)
    public void testCantFreeMoreThanCreated() {
        LockObjectDealer<String> locker = new LockObjectDealer<>();

        locker.createLock("foo");
        locker.freeLock("foo");
        locker.freeLock("foo");
    }
}
