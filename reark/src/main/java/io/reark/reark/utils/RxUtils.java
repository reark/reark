package io.reark.reark.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Pawel Polanski on 6/4/15.
 */
public final class RxUtils {

    private RxUtils() {
        throw new AssertionError();
    }

    @NonNull
    public static <T> Observable<List<T>> toObservableList(@NonNull List<Observable<T>> observables) {
        Preconditions.checkNotNull(observables, "Observable List cannot be null.");

        return Observable.combineLatest(observables, RxUtils::toList);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    static <T> List<T> toList(@NonNull Object... args) {
        Preconditions.checkNotNull(args, "Arguments cannot be null.");

        final List<T> list = new ArrayList<>();
        for (Object item : args) {
            list.add((T) item);
        }
        return list;
    }

}
