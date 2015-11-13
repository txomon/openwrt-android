package com.txomon.openwrt.rpc;

import java.util.Map;

public interface UbusClientInterface {
    public Object request(String method, String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    public Object call(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException;

    public Object list(String ubusObject) throws UbusRpcException;

}
