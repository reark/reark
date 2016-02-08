package io.reark.rxgithubapp.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Preconditions;

/**
 * Created by antti on 17.1.2016.
 */
public abstract class StoreBase<T, U> extends SingleItemContentProviderStore<T, U> {

    private final Gson gson;

    public StoreBase(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver);

        Preconditions.checkNotNull(gson, "Gson cannot be null.");

        this.gson = gson;
    }

    protected Gson getGson() {
        return gson;
    }
}
