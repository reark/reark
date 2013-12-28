package com.timotuominen.app.utils;

import java.lang.reflect.Field;

/**
 * Created by tehmou on 12/25/13.
 */
public class ViewModel {
    public void unsubscribe() {
        SubscriptionAnnotationUtils.unsubscribeAllAnnotated(this);
    }
}
