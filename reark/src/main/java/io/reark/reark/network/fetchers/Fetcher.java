package io.reark.reark.network.fetchers;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by ttuo on 16/04/15.
 */
public interface Fetcher {
    void fetch(Intent intent);
    Uri getServiceUri();
}
