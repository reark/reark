// INetworkServiceListener.aidl
package com.tehmou.rxbookapp.network;

interface INetworkServiceListener {
    void handleStateChange(String uri, int networkRequestState);
}
