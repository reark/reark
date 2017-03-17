package io.reark.reark.data.stores.cores.operations;

import android.content.ContentProviderResult;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class CoreOperationResult {

    private final int id;

    @NonNull
    private final Uri uri;

    private final boolean success;

    public CoreOperationResult(@NonNull CoreOperation operation, boolean success) {
        this.id = operation.id();
        this.uri = operation.uri();
        this.success = success;
    }

    public CoreOperationResult(@NonNull ContentProviderResult result, @NonNull CoreOperation operation) {
        this.id = operation.id();
        this.uri = operation.uri();
        this.success = result.count == null || result.count > 0;
    }

    public int id() {
        return id;
    }

    @NonNull
    public Uri uri() {
        return uri;
    }

    public boolean success() {
        return success;
    }

}
