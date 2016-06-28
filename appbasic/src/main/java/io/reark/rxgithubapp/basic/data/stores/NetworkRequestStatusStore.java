package io.reark.rxgithubapp.basic.data.stores;

import io.reark.reark.data.stores.MemoryStore;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import rx.Observable;

/**
 * Created by ttuo on 27/06/16.
 */
public class NetworkRequestStatusStore extends MemoryStore<Integer, NetworkRequestStatus> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore() {
        super(networkRequestStatus -> networkRequestStatus.getUri().hashCode());
    }

    @Override
    public void put(NetworkRequestStatus item) {
        super.put(item);
        Log.d(TAG, "network put: " + item.getUri().hashCode());
    }

    @Override
    public Observable<NetworkRequestStatus> getStream(Integer id) {
        Log.d(TAG, "network get: " + id);
        return super.getStream(id);
    }
}
