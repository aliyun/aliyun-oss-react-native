package com.reactlibrary.utils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.facebook.react.bridge.ReadableMap;

public class ConfigUtils {

    /**
     * Auth initAuthConfig
     * @param configuration
     * @return
     */
    public static ClientConfiguration initAuthConfig(ReadableMap configuration) {
       ClientConfiguration conf = new ClientConfiguration();
       conf.setConnectionTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
       conf.setSocketTimeout(configuration.getInt("timeoutIntervalForRequest") * 1000);
       conf.setMaxConcurrentRequest(configuration.getInt("maxRetryCount"));
       conf.setMaxErrorRetry(configuration.getInt("maxRetryCount"));
       return conf;
   }
}
