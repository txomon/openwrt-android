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
import java.util.List;
import java.util.Map;

public class UbusRpcClient implements UbusClientInterface {
    private static final String TAG = "UbusRpcClient";
    JSONRPC2Session session = null;
    private String STATUS_ARRAY[] = {
            "OK",
            "Invalid command",
            "Invalid argument",
            "Method not found",
            "Not found",
            "No data",
            "Permission denied",
            "Timeout",
            "Not supported",
            "Unknown error",
            "Connection failed",
    };
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
        JSONRPC2Response jsonRpcResponse;
        Object response;

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
            jsonRpcResponse = session.send(request);
        } catch (JSONRPC2SessionException e) {
            Log.e(TAG, "Send error: " + request.toJSONString());
            Log.e(TAG, e.getMessage());
            throw new UbusRpcException(e.getMessage());
        }

        if (!jsonRpcResponse.indicatesSuccess()) {
            Log.e(TAG, "Response not successful: " + request.toJSONString());
            Log.e(TAG, jsonRpcResponse.getError().getMessage());
            throw new UbusRpcException(jsonRpcResponse.getError().getMessage());
        }

        response = jsonRpcResponse.getResult();
        if (response == null) {
            throw new UbusRpcException("Null response");
        }
        return response;
    }

    public Object call(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException {
        List jsonListRpcResponse;
        Object response;
        long returnCode;

        if (ubusObject.length() == 0 || ubusMethod.length() == 0)
            throw new UbusRpcException("Object and method should not be empty");

        response = this.request("call", ubusObject, ubusMethod, arguments);
        if (!(response instanceof List)) {
            throw new UbusRpcException("Response not a list");
        }
        jsonListRpcResponse = (List) response;
        if (!(jsonListRpcResponse.get(0) instanceof Long)) {
            throw new UbusRpcException("Return code not Long: " + jsonListRpcResponse.get(0).getClass());
        }
        returnCode = (Long) jsonListRpcResponse.get(0);
        if (returnCode != 0) {
            throw new UbusRpcException(STATUS_ARRAY[(int) returnCode]);
        }
        if (jsonListRpcResponse.size() > 2) {
            throw new UbusRpcException("Response not ret+res");
        }
        return jsonListRpcResponse.get(1);
    }

    public Map<String, Map<String, Map<String, String>>> list(String path) throws UbusRpcException {
        Object response;
        Map<String, Map<String, Map<String, String>>> castedResponse;
        if (path == null)
            response = this.request("list", "*", null, null);
        else
            response = this.request("list", path, null, null);
        try {
            castedResponse = (Map<String, Map<String, Map<String, String>>>) response;
        } catch (ClassCastException e) {
            throw new UbusRpcException("List response doesn't comply with spec");
        }
        return castedResponse;
    }
}
