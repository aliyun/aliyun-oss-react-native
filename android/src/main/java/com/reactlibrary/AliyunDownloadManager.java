package com.reactlibrary;

import android.os.Environment;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AliyunDownloadManager {
    private OSS mOSS;

    /**
     * AliyunDownloadManager
     * @param oss
     */
    public AliyunDownloadManager(OSS oss) {
        mOSS = oss;
    }

    public void asyncDownload(final ReactContext context,String bucketName, String ossFile, String updateDate, ReadableMap options, final Promise promise) {
        GetObjectRequest get = new GetObjectRequest(bucketName, ossFile);

        String xOssPositon = options.getString("x-oss-process");
        //process image
        get.setxOssProcess(xOssPositon);

        OSSAsyncTask task = mOSS.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {

                Log.d("Content-Length", "" + result.getContentLength());

                InputStream inputStream = result.getObjectContent();
                long resultLength = result.getContentLength();

                byte[] buffer = new byte[2048];
                int len;

                FileOutputStream outputStream = null;
                String localImgURL = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/ImgCache/" +
                        System.currentTimeMillis() +
                        ".jpg";
                Log.d("localImgURL", localImgURL);
                File cacheFile = new File(localImgURL);
                if (!cacheFile.exists()) {
                    cacheFile.getParentFile().mkdirs();
                    try {
                        cacheFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        promise.reject("DownloadFaile", e);
                    }
                }
                long readSize = cacheFile.length();
                try {
                    outputStream = new FileOutputStream(cacheFile, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    promise.reject("DownloadFaile", e);
                }
                if (resultLength == -1) {
                    promise.reject("DownloadFaile", "message:lengtherror");
                }

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                       // resove download data
                        try {
                            outputStream.write(buffer, 0, len);
                            readSize += len;

                            String str_currentSize = Long.toString(readSize);
                            String str_totalSize = Long.toString(resultLength);
                            WritableMap onProgressValueData = Arguments.createMap();
                            onProgressValueData.putString("currentSize", str_currentSize);
                            onProgressValueData.putString("totalSize", str_totalSize);
                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit("downloadProgress", onProgressValueData);

                        } catch (IOException e) {
                            e.printStackTrace();
                            promise.reject("DownloadFaile", e);
                        }
                    }
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    promise.reject("DownloadFaile", e);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            promise.reject("DownloadFaile", e);
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            promise.reject("DownloadFaile", e);
                        }
                    }
                    promise.resolve(localImgURL);
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });
    }
}
