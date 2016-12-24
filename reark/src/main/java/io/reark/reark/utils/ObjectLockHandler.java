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

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static io.reark.reark.utils.Preconditions.checkNotNull;

/**
 * Util class for locking based on given objects. Equal objects will share a lock, so that
 * access to a section can be restricted in cases where simultaneous usage of two equal
 * objects could cause concurrency issues.
 */
public final class ObjectLockHandler<T> {

    @NonNull
    private final Object locksEditLock = new Object();

    @NonNull
    private final Map<T, LockHolder> locks = new HashMap<>();

    private static class LockHolder {
        final Semaphore lockObject = new Semaphore(1);
        int counter = 1;
    }

    public void acquire(@NonNull final T object) throws InterruptedException {
        checkNotNull(object);

        createLock(object);

        locks.get(object).lockObject.acquire();
    }

    public void release(@NonNull final T object) {
        checkNotNull(object);

        if (!locks.containsKey(object)) {
            throw new IllegalStateException("Tried to release without acquiring first: " + object);
        }

        locks.get(object).lockObject.release();

        freeLock(object);
    }

    private synchronized void createLock(@NonNull final T object) {
        synchronized (locksEditLock) {
            if (locks.containsKey(object)) {
                updateLockCounter(object, 1);
            } else {
                locks.put(object, new LockHolder());
            }
        }
    }

    private void freeLock(@NonNull final T object) {
        synchronized (locksEditLock) {
            if (locks.get(object).counter == 1) {
                locks.remove(object);
            } else {
                updateLockCounter(object, -1);
            }
        }
    }

    private void updateLockCounter(@NonNull final T object, final int diff) {
        locks.get(object).counter += diff;
    }
}
