package io.reark.reark.utils;

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

    private static String getThreadSignature(final String tag) {
        return "Thread{" + Thread.currentThread().getName() + "}/" + tag;
    }
}
