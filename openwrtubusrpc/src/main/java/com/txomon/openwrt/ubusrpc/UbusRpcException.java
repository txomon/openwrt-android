package com.txomon.openwrt.ubusrpc;

public class UbusRpcException extends Exception {
    UbusRpcException() {
    }

    UbusRpcException(String message) {
        super(message);
    }
}
