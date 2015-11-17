package com.txomon.openwrt.android;

import com.txomon.openwrt.ubusrpc.UbusClient;
import com.txomon.openwrt.ubusrpc.UbusRpcException;

import java.util.Map;

import rx.Observer;

public interface UbusRpcFragmentInteractionListenerInterface {
    UbusClient getCurrentClient();

    Object makeUbusRpcClientCall(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    Object makeUbusClientCall(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    Observer<Object> getCallResultObserver();

    void handleCallError(String message);
}
