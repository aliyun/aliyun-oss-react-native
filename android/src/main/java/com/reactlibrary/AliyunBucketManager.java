package com.reactlibrary;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CannedAccessControlList;
import com.alibaba.sdk.android.oss.model.CreateBucketRequest;
import com.alibaba.sdk.android.oss.model.CreateBucketResult;
import com.alibaba.sdk.android.oss.model.DeleteBucketRequest;
import com.alibaba.sdk.android.oss.model.DeleteBucketResult;
import com.alibaba.sdk.android.oss.model.GetBucketACLRequest;
import com.alibaba.sdk.android.oss.model.GetBucketACLResult;
import com.alibaba.sdk.android.oss.model.ListBucketsRequest;
import com.alibaba.sdk.android.oss.model.ListBucketsResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

public class AliyunBucketManager {
    private OSS mOSS;

    /**
     * AliyunBucketManager
     * @param oss
     */
    public AliyunBucketManager(OSS oss) {
        mOSS = oss;
    }

    /**
     * asyncCreateBucket
     * @param bucketName
     * @param acl
     * @param region
     * @param promise
     */
    public void asyncCreateBucket(String bucketName, String acl, String region, final Promise promise) {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setBucketACL(CannedAccessControlList.parseACL(acl));
        createBucketRequest.setLocationConstraint(region);
        OSSAsyncTask createTask = mOSS.asyncCreateBucket(createBucketRequest, new OSSCompletedCallback<CreateBucketRequest, CreateBucketResult>() {
            @Override
            public void onSuccess(CreateBucketRequest createBucketRequest, CreateBucketResult createBucketResult) {
                Log.d("locationConstraint", createBucketRequest.getLocationConstraint());
                promise.resolve("createBucket success");
            }

            @Override
            public void onFailure(CreateBucketRequest createBucketRequest, ClientException e, ServiceException e1) {
               PromiseExceptionManager.resolvePromiseException(e,e1,promise);
            }
        });
    }

    /**
     * asyncGetBucketACL
     * @param bucketName
     * @param promise
     */
    public void asyncGetBucketACL(String bucketName, final Promise promise) {
        GetBucketACLRequest getBucketACLRequest = new GetBucketACLRequest(bucketName);
        OSSAsyncTask getBucketAclTask = mOSS.asyncGetBucketACL(getBucketACLRequest, new OSSCompletedCallback<GetBucketACLRequest, GetBucketACLResult>() {
            @Override
            public void onSuccess(GetBucketACLRequest getBucketACLRequest, GetBucketACLResult getBucketACLResult) {
                Log.d("BucketAcl", getBucketACLResult.getBucketACL());
                Log.d("Owner", getBucketACLResult.getBucketOwner());
                Log.d("ID", getBucketACLResult.getBucketOwnerID());

                WritableMap map = Arguments.createMap();
                map.putString("BucketAcl", getBucketACLResult.getBucketACL());
                map.putString("Owner", getBucketACLResult.getBucketOwner());
                map.putString("ID", getBucketACLResult.getBucketOwnerID());
                promise.resolve(map);
            }

            @Override
            public void onFailure(GetBucketACLRequest getBucketACLRequest, ClientException e, ServiceException e1) {
                PromiseExceptionManager.resolvePromiseException(e, e1, promise);
            }
        });
    }

    /**
     * asyncListBuckets
     * @param promise
     */
    public void asyncListBuckets(final Promise promise) {

        ListBucketsRequest listBuckets = new ListBucketsRequest();

        OSSAsyncTask task = mOSS.asyncListBuckets(listBuckets, new OSSCompletedCallback<ListBucketsRequest, ListBucketsResult>() {
            @Override
            public void onSuccess(ListBucketsRequest request, ListBucketsResult result) {
                Log.d("listBuckets", "Success!");
                WritableMap map = Arguments.createMap();
                for (int i = 0;i < result.getBuckets().size();i++) {
                    map.putString("Bucket"+i,result.getBuckets().get(i).toString());
                    Log.d("asyncListBuckets","bucket:"+result.getBuckets().get(i));
                }
                promise.resolve(map);
            }

            @Override
            public void onFailure(ListBucketsRequest request, ClientException clientException, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientException,serviceException,promise);
            }
        });
    }

    /**
     * asyncDeleteBucket
     * @param bucketName
     * @param promise
     */
    public void asyncDeleteBucket(String bucketName,final Promise promise) {
        DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
        OSSAsyncTask deleteBucketTask = mOSS.asyncDeleteBucket(deleteBucketRequest, new OSSCompletedCallback<DeleteBucketRequest, DeleteBucketResult>() {
            @Override
            public void onSuccess(DeleteBucketRequest request, DeleteBucketResult result) {
                promise.resolve("Delete Bucket Success!!!!!");
            }
            @Override
            public void onFailure(DeleteBucketRequest request, ClientException clientException, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientException,serviceException,promise);
            }
        });
    }
}
