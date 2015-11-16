package com.txomon.openwrt.rpc;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UbusObject {
    private final static String TAG = "UbusObject";
    public String object;
    public Map<String, Map<String, Class>> spec;

    public UbusObject(String object, Map<String, Map<String, String>> objectSpec) {
        this.object = object;
        spec = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> methodEntry : objectSpec.entrySet()) {
            String specMethod = methodEntry.getKey();
            Map<String, String> specArgs = methodEntry.getValue();
            Map<String, Class> arguments = new HashMap<>();

            for (Map.Entry<String, String> argumentEntry : specArgs.entrySet()) {
                String specArg = argumentEntry.getKey();
                Class type;
                switch (argumentEntry.getValue()) {
                    case "boolean":
                        type = Boolean.class;
                        break;
                    case "string":
                        type = String.class;
                        break;
                    case "number":
                        type = Number.class;
                        break;
                    case "array":
                        type = List.class;
                        break;
                    case "object":
                        type = Map.class;
                        break;
                    default:
                        Log.wtf(TAG, "Error in initialization, arg " + specArg +
                                " has unknown type " + argumentEntry.getValue());
                        continue;
                }
                arguments.put(specArg, type);
            }
            spec.put(specMethod, arguments);
        }
    }

    public static UbusObject fromSpec(String object, Map<String, Map<String, String>> objectSpec) {
        return new UbusObject(object, objectSpec);
    }

    public static Map<String, UbusObject> fromList(Map<String, Map<String, Map<String, String>>> list) {
        Map<String, UbusObject> objects = new HashMap<>();
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : list.entrySet()) {
            objects.put(entry.getKey(), UbusObject.fromSpec(entry.getKey(), entry.getValue()));
        }
        return objects;
    }

}
