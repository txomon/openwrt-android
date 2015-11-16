package com.txomon.openwrt.android;

import com.txomon.openwrt.rpc.UbusRpcException;

import java.util.Map;

import rx.Observer;

public interface UbusRpcFragmentInteractionListenerInterface {
    Object makeUbusRpcClientCall(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    Object makeUbusClientCall(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    Observer<Object> getCallResultObserver();

    void handleCallError(String message);
}
