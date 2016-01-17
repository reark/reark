package io.reark.rxgithubapp.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Inject;

import io.reark.reark.data.store.SingleItemContentProviderStore;

/**
 * Created by antti on 17.1.2016.
 */
public abstract class StoreBase<T, U> extends SingleItemContentProviderStore<T, U> {

    @Inject
    Gson gson;

    public StoreBase(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    protected Gson getGson() {
        return gson;
    }
}
