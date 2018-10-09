package com.reactlibrary;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CopyObjectRequest;
import com.alibaba.sdk.android.oss.model.CopyObjectResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

public class AliyunObjectManager {
    private OSS mOSS;

    /**
     * AliyunObjectManager contructro
     * @param oss
     */
    public AliyunObjectManager(OSS oss) {
        mOSS = oss;
    }

    /**
     * asyncHeadObject
     * @param bucketName
     * @param objectKey
     * @param promise
     */
    public void asyncHeadObject(String bucketName, String objectKey, final Promise promise) {

        HeadObjectRequest head = new HeadObjectRequest(bucketName, objectKey);
        OSSAsyncTask task = mOSS.asyncHeadObject(head, new OSSCompletedCallback<HeadObjectRequest, HeadObjectResult>() {
            @Override
            public void onSuccess(HeadObjectRequest headObjectRequest, HeadObjectResult headObjectResult) {
                float objectSize = headObjectResult.getMetadata().getContentLength();
                String objectContentType = headObjectResult.getMetadata().getContentType();
                Log.d("headObject", "object Size: " + headObjectResult.getMetadata().getContentLength());
                Log.d("headObject", "object Content Type: " + headObjectResult.getMetadata().getContentType());
                WritableMap map = Arguments.createMap();
                map.putDouble("objectSize", objectSize);
                map.putString("objectContentType", objectContentType);
                promise.resolve(map);
            }

            @Override
            public void onFailure(HeadObjectRequest headObjectRequest, ClientException e, ServiceException e1) {
                PromiseExceptionManager.resolvePromiseException(e,e1,promise);
            }
        });
    }

    /**
     * doesObjectExist
     * @param bucketName
     * @param objectKey
     * @param promise
     */
    public void doesObjectExist(String bucketName,String objectKey,final Promise promise) {
        try {
            if (mOSS.doesObjectExist(bucketName,objectKey)) {
                Log.d("doesObjectExist", "object exist.");
                promise.resolve("object exist");
            } else {
                Log.d("doesObjectExist", "object does not exist.");
                promise.resolve("object does not exist");
            }
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
            promise.reject(e);
        } catch (ServiceException e) {
            // 服务异常
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("RequestId", e.getRequestId());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
            promise.reject(e);
        }
    }

    /**
     * asyncListObjects
     * @param bucketName
     * @param opitons {delimiter|prefix|marker|maxkeys}
     * @param promise
     */
    public void asyncListObjects(String bucketName, ReadableMap options, final Promise promise) {
        ListObjectsRequest listObjects = new ListObjectsRequest(bucketName);

        if(options.hasKey("prefix")) {
            listObjects.setPrefix(options.getString("prefix"));
        }

        if(options.hasKey("delimiter")) {
            listObjects.setDelimiter(options.getString("delimiter"));
        }

        if(options.hasKey("marker")) {
            listObjects.setMarker(options.getString("delimiter"));
        }

        if(options.hasKey("maxkeys")) {
            listObjects.setMaxKeys(options.getInt(String.valueOf(options.getInt("maxkeys"))));
        }

        // set success 、set fail 、set async request
        OSSAsyncTask task = mOSS.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                WritableMap map = Arguments.createMap();

                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    map.putString("objectKey"+i , result.getObjectSummaries().get(i).getKey());
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());
                }
                promise.resolve(map);
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });

    }

    public void asyncCopyObject(String srcBucketName,
                                String srcObjectKey,
                                String desBucketName,
                                String destObjectKey,
                                ReadableMap options,
                                final Promise promise) {

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcBucketName, srcObjectKey,
                desBucketName, destObjectKey);
//
//        set copy metadata
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType("application/octet-stream");
//        copyObjectRequest.setNewObjectMetadata(objectMetadata);

        // async copy
        OSSAsyncTask copyTask = mOSS.asyncCopyObject(copyObjectRequest, new OSSCompletedCallback<CopyObjectRequest, CopyObjectResult>() {
            @Override
            public void onSuccess(CopyObjectRequest request, CopyObjectResult result) {
                Log.d("copyObject", "copy success!");
                promise.resolve("copy success!");
            }

            @Override
            public void onFailure(CopyObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
              PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });
    }

    /**
     * asyncDeleteObject
     * @param bucketName
     * @param objectKey
     * @param promise
     */
    public void asyncDeleteObject(String bucketName, String objectKey, final Promise promise) {

        // async delete request
        DeleteObjectRequest delete = new DeleteObjectRequest(bucketName, objectKey);

        OSSAsyncTask deleteTask = mOSS.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.d("asyncCopyAndDelObject", "success!");
                promise.resolve("delete success!");
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });
    }
}
