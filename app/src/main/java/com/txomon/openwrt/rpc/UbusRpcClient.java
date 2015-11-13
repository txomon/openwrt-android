package com.txomon.openwrt.rpc;


import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UbusRpcClient implements UbusClientInterface {
    private static final String TAG = "UbusRpcClient";
    JSONRPC2Session session = null;
    private URL serverUrl = null;

    public UbusRpcClient(String endpoint) {
        try {
            serverUrl = new URL(endpoint);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed url: '" + endpoint + "'");
            Log.e(TAG, e.getMessage());
        }
        session = new JSONRPC2Session(serverUrl);
    }

    public Object request(String method, String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException {
        ArrayList<Object> rpc_params = new ArrayList<>();
        JSONRPC2Request request;
        JSONRPC2Response response;

        rpc_params.add("00000000000000000000000000000000");
        rpc_params.add(ubusObject);
        if (ubusMethod != null)
            rpc_params.add(ubusMethod);
        if (arguments != null)
            rpc_params.add(arguments);
        else
            rpc_params.add(new HashMap<>());

        request = new JSONRPC2Request(method, rpc_params, 0);
        try {
            response = session.send(request);
        } catch (JSONRPC2SessionException e) {
            Log.e(TAG, "Send error: " + request.toJSONString());
            Log.e(TAG, e.getMessage());
            throw new UbusRpcException();
        }

        if (!response.indicatesSuccess()) {
            Log.e(TAG, "Response not successful: " + request.toJSONString());
            Log.e(TAG, response.getError().getMessage());
            throw new UbusRpcException();
        }
        return response.getResult();
    }

    public Object call(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException {
        return this.request("call", ubusObject, ubusMethod, arguments);
    }

    public Object list(String path) throws UbusRpcException {
        if (path == null)
            return this.request("list", "*", null, null);
        else
            return this.request("list", path, null, null);
    }
}
