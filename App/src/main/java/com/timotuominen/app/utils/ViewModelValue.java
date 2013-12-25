package com.timotuominen.app.utils;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.util.functions.Action1;

/**
 * Created by ttuo on 12/25/13.
 */
public class ViewModelValue<T> {
    private T lastValue;
    final private Subject<T, T> value = PublishSubject.create();
    final private Observable<T> distinctValue = value.distinctUntilChanged();
    final private SubscriptionManager subscriptionManager = new SubscriptionManager();

    public void setValue(T value) {
        this.lastValue = value;
        this.value.onNext(value);
    }

    private Observable<T> getValueObservable() {
        return Observable.merge(Observable.from(lastValue), distinctValue);
    }

    public void subscribe(Observer<? super T> observer) {
        subscriptionManager.add(getValueObservable().subscribe(observer));
    }

    public void subscribe(final Action1<? super T> onNext) {
        subscriptionManager.add(getValueObservable().subscribe(onNext));
    }

    public void unsubscribe() {
        subscriptionManager.unsubscribe();
    }
}
