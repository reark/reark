// INetworkService.aidl
package com.tehmou.rxbookapp.network;

import com.tehmou.rxbookapp.network.INetworkServiceListener;

interface INetworkService {
    void addStateListener(INetworkServiceListener listener);
}
