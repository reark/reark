package io.reark.reark.data.stores;

import android.support.annotation.NonNull;

/**
 * A simple store containing String values tracked with Integer keys.
 */
public class SimpleMockStore extends ContentProviderStore<Integer, String, String> {

    public static final String NONE = "";

    public SimpleMockStore(@NonNull final SimpleMockStoreCore core) {
        super(core,
                SimpleMockStore::getIdFor,
                value -> value != null ? value : NONE,
                () -> NONE);
    }

    @NonNull
    public static Integer getIdFor(@NonNull final String item) {
        return item.hashCode();
    }

}