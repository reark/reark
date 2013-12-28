package com.timotuominen.app.utils;

import rx.Observer;
import rx.util.functions.Action1;

/**
 * Created by tehmou on 12/28/13.
 */
public interface ViewModelValue<T> {
    public void subscribe(Observer<? super T> observer);
    public void subscribe(final Action1<? super T> onNext);
}
