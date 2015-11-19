package com.txomon.openwrt.ubusrpc;

import android.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class UbusClient {
    private final static String TAG = "UbusClient";

    private UbusRpcClient rpcClient;
    private Map<String, UbusObject> ubusSpec;

    public UbusClient(String endpoint) {
        rpcClient = new UbusRpcClient(endpoint);
        ubusSpec = new HashMap<>();
    }

    public boolean check(String ubusObject, String ubusMethod, Map<String, Object> arguments) {
        Set<String> allArgs = new HashSet<>();

        UbusObject object = ubusSpec.get(ubusObject);

        if (object == null)
            return false;
        Map<String, Class> argumentSpec = object.spec.get(ubusMethod);
        if (arguments == null)
            return argumentSpec.size() == 0;

        allArgs.addAll(argumentSpec.keySet());
        if (allArgs.addAll(arguments.keySet()))
            return false;

        for (String arg : allArgs) {
            Class type = argumentSpec.get(arg);
            Object argument = arguments.get(arg);
            if (type == null || argument == null) {
                Log.d(TAG, "Type or/and argument are null arg:" + type + "argument:" + argument);
                return false;
            }
            if (!type.isAssignableFrom(argument.getClass())) {
                Log.d(TAG, "Argument not complying: " + arg + " is not " + type.toString());
                return false;
            }
        }
        return true;
    }

    public Object call(String ubusObject, String ubusMethod, Map arguments) throws UbusRpcException {
        if (!check(ubusObject, ubusMethod, arguments))
            throw new UbusRpcException("Method doesn't comply");
        return rpcClient.call(ubusObject, ubusMethod, arguments);
    }

    public boolean update() throws UbusRpcException {
        Map<String, Map<String, Map<String, String>>> rpcSpec = rpcClient.list(null);
        ubusSpec = UbusObject.fromList(rpcSpec);
        return true;
    }

    public List<String> getObjects() {
        List objects = new ArrayList<String>();
        objects.addAll(ubusSpec.keySet());
        Collections.sort(objects);
        return objects;
    }

    public List<String> getMethods(String object) {
        List methods = new ArrayList<String>();
        UbusObject ubusObject = ubusSpec.get(object);
        methods.addAll(ubusObject.spec.keySet());
        Collections.sort(methods);
        return methods;
    }

    public SortedMap<String, Class> getArguments(String object, String method) {
        SortedMap<String, Class> arguments = new TreeMap();
        UbusObject ubusObject = ubusSpec.get(object);
        arguments.putAll(ubusObject.spec.get(method));
        return arguments;
    }
}
