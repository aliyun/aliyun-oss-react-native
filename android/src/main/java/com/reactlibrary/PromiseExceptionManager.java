package com.reactlibrary;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.facebook.react.bridge.Promise;

public class PromiseExceptionManager {

    /**
     * resolvePromiseException
     * @param clientExcepion
     * @param serviceException
     * @param promise
     */
    public static void resolvePromiseException(ClientException clientExcepion, ServiceException serviceException, final Promise promise) {
        if (clientExcepion != null) {
            clientExcepion.printStackTrace();
            promise.reject(clientExcepion);
        }
        if (serviceException != null) {
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
            promise.reject(serviceException);
        }
    }
}