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


import io.reactivex.functions.Consumer;

/**
 * Thread-aware logging util
 */
public class Log {

    public static void v(String tag, String msg) {
        android.util.Log.v(getThreadSignature(tag), msg);
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(getThreadSignature(tag), msg);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(getThreadSignature(tag), msg);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(getThreadSignature(tag), msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        android.util.Log.w(getThreadSignature(tag), msg, tr);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(getThreadSignature(tag), msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(getThreadSignature(tag), msg, tr);
    }

    public static Consumer<Throwable> onError(String tag) {
        return onError(tag, "Error");
    }

    public static Consumer<Throwable> onError(String tag, String msg) {
        return error -> Log.e(tag, msg, error);
    }

    private static String getThreadSignature(final String tag) {
        return "Thread{" + Thread.currentThread().getName() + "}/" + tag;
    }
}
