package com.timotuominen.app.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ttuo on 12/25/13.
 */
public class SubscriptionAnnotationUtils {
    final static String TAG =  SubscriptionAnnotationUtils.class.getCanonicalName();
    final static String UNSUBSCRIBE_METHOD_NAME = "unsubscribe";

    static public void unsubscribeAllAnnotated(Object object) {
        for (Field field : object.getClass().getFields()) {
            if (field.isAnnotationPresent(Unsubscribable.class)) {
                try {
                    Object fieldValue = field.get(object);
                    try {
                        Method method = fieldValue.getClass().getMethod(UNSUBSCRIBE_METHOD_NAME);
                        try {
                            method.invoke(fieldValue);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.toString());
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, e.toString());
                        } catch (InvocationTargetException e) {
                            Log.e(TAG, e.toString());
                        }
                    } catch (NoSuchMethodException e) {
                        Log.e(TAG, e.toString());
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }
}
