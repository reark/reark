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

import java.util.HashMap;
import java.util.Map;

/**
 * Util class for providing objects to be used as synchronization locks. Equal objects will
 * share the same lock object, so that it can be used to synchronize sections where simultaneous
 * usage of two equal objects could cause concurrency issues.
 */
public class LockObjectDealer<T> {
    private final Object locksEditLock = new Object();
    private final Map<T, LockObjectHolder> locks = new HashMap<>();

    private class LockObjectHolder {
        public final Object lockObject = new Object();
        public int counter = 1;
    }

    public void createLock(T object) {
        synchronized (locksEditLock) {
            if (!locks.containsKey(object)) {
                locks.put(object, new LockObjectHolder());
            } else {
                updateLockCounter(object, 1);
            }
        }
    }

    public Object getLock(T object) {
        return locks.get(object).lockObject;
    }

    public void freeLock(T object) {
        synchronized (locksEditLock) {
            if (locks.get(object).counter == 1) {
                locks.remove(object);
            } else {
                updateLockCounter(object, -1);
            }
        }
    }

    private void updateLockCounter(T object, int diff) {
        locks.get(object).counter += diff;
    }
}
