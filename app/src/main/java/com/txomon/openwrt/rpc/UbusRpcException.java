package com.txomon.openwrt.rpc;

public class UbusRpcException extends Exception {
    UbusRpcException() {
    }

    UbusRpcException(String message) {
        super(message);
    }
}
