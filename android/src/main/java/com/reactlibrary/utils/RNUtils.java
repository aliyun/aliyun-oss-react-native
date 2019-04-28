package com.reactlibrary.utils;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin.bai on 2019-04-28.
 */
public class RNUtils {
    /**
     * 将ReadableMap对象转为Map对象，只支持String类型的value
     * @param param
     * @return
     */
    public static Map<String, String> convertMap(ReadableMap param){
        Map<String, String> result = new HashMap<>();
        ReadableMapKeySetIterator iterator = param.keySetIterator();
        while(iterator.hasNextKey()){
            String key = iterator.nextKey();
            if (param.getType(key) == ReadableType.String){
                result.put(key, param.getString(key));
            }
        }
        return result;
    }
}
