package com.reactlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.AppendObjectRequest;
import com.alibaba.sdk.android.oss.model.AppendObjectResult;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.ListPartsRequest;
import com.alibaba.sdk.android.oss.model.ListPartsResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.alibaba.sdk.android.oss.model.UploadPartRequest;
import com.alibaba.sdk.android.oss.model.UploadPartResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.reactlibrary.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AliyunUploadManager {

    private OSS mOSS;

    /**
     * AliyunUploadManager contructor
     * @param oss
     */
    public AliyunUploadManager(OSS oss) {
        mOSS = oss;
    }

    /**
     * asyncUpload
     * @param context
     * @param bucketName
     * @param ossFile
     * @param sourceFile
     * @param options
     * @param promise
     */
    public void asyncUpload(final ReactContext context, String bucketName, String ossFile, String sourceFile, ReadableMap options, final Promise promise) {
        // Content to file:// start
        Uri selectedVideoUri = Uri.parse(sourceFile);

        // 1. content uri -> file path
        // 2. inputstream -> temp file path
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getCurrentActivity().getContentResolver().query(selectedVideoUri, proj, null, null, null);
            if (cursor == null) sourceFile = selectedVideoUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            sourceFile = cursor.getString(column_index);
        } catch (Exception e) {
            sourceFile = FileUtils.getFilePathFromURI(context.getCurrentActivity(), selectedVideoUri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // init upload request
        PutObjectRequest put = new PutObjectRequest(bucketName, ossFile, sourceFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        put.setMetadata(metadata);

        // set callback
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                String str_currentSize = Long.toString(currentSize);
                String str_totalSize = Long.toString(totalSize);
                WritableMap onProgressValueData = Arguments.createMap();
                onProgressValueData.putString("currentSize", str_currentSize);
                onProgressValueData.putString("totalSize", str_totalSize);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("uploadProgress", onProgressValueData);
            }
        });

        OSSAsyncTask task = mOSS.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                promise.resolve("UploadSuccess");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });
        Log.d("AliyunOSS", "OSS uploadObjectAsync ok!");
    }

    /**
     * asyncAppendObject
     * @param bucketName
     * @param objectKey
     * @param uploadFilePath
     * @param options
     * @param promise
     */
    public void asyncAppendObject(final ReactContext context,String bucketName,String objectKey,String uploadFilePath,ReadableMap options,final Promise promise) {

        // Content to file:// start
        Uri selectedVideoUri = Uri.parse(uploadFilePath);

        // 1. content uri -> file path
        // 2. inputstream -> temp file path
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getCurrentActivity().getContentResolver().query(selectedVideoUri, proj, null, null, null);
            if (cursor == null) uploadFilePath = selectedVideoUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            uploadFilePath = cursor.getString(column_index);
        } catch (Exception e) {
            uploadFilePath = FileUtils.getFilePathFromURI(context.getCurrentActivity(), selectedVideoUri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        AppendObjectRequest append = new AppendObjectRequest(bucketName, objectKey, uploadFilePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        append.setMetadata(metadata);

        //set appendpostions
        int nextPositon = options.getInt("appendPostions");
        append.setPosition(nextPositon);

        append.setProgressCallback(new OSSProgressCallback<AppendObjectRequest>() {
            @Override
            public void onProgress(AppendObjectRequest request, long currentSize, long totalSize) {
                Log.d("AppendObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                // add event
                String str_currentSize = Long.toString(currentSize);
                String str_totalSize = Long.toString(totalSize);
                WritableMap onProgressValueData = Arguments.createMap();
                onProgressValueData.putString("currentSize", str_currentSize);
                onProgressValueData.putString("totalSize", str_totalSize);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("uploadProgress", onProgressValueData);
            }
        });

        OSSAsyncTask task = mOSS.asyncAppendObject(append, new OSSCompletedCallback<AppendObjectRequest, AppendObjectResult>() {
            @Override
            public void onSuccess(AppendObjectRequest request, AppendObjectResult result) {
                Log.d("AppendObject", "AppendSuccess");
                Log.d("NextPosition", "" + result.getNextPosition());
                WritableMap map = Arguments.createMap();
                map.putString("AppendObject","AppendSuccess");
                map.putDouble("NextPosition", result.getNextPosition());
                promise.resolve(map);
            }
            @Override
            public void onFailure(AppendObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion,serviceException,promise);
            }
        });
    }

    /**
     * asyncResumableUpload
     *
     * @param bucketName
     * @param objectKey
     * @param uploadFilePath
     * @param options
     * @param promise
     */
    public void asyncResumableUpload(final ReactContext context, String bucketName, String objectKey, String uploadFilePath, ReadableMap options, final Promise promise) {

        ResumableUploadRequest request = new ResumableUploadRequest(bucketName, objectKey, uploadFilePath);

        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                Log.d("resumableUpload", "currentSize: " + currentSize + " totalSize: " + totalSize);
                // add event
                String str_currentSize = Long.toString(currentSize);
                String str_totalSize = Long.toString(totalSize);
                WritableMap onProgressValueData = Arguments.createMap();
                onProgressValueData.putString("currentSize", str_currentSize);
                onProgressValueData.putString("totalSize", str_totalSize);
                context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("uploadProgress", onProgressValueData);
            }
        });

        OSSAsyncTask resumableTask = mOSS.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                promise.resolve("resumableUpload success");
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                PromiseExceptionManager.resolvePromiseException(clientExcepion, serviceException, promise);
            }
        });
    }

    /**
     * initMultipartUpload
     * @param bucketName
     * @param objectKey
     * @param promise
     */
    public void initMultipartUpload(String bucketName,String objectKey,final Promise promise) {
        String uploadId;
        InitiateMultipartUploadRequest init = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult initResult = null;
        try {
            initResult = mOSS.initMultipartUpload(init);
            uploadId = initResult.getUploadId();
            promise.resolve(uploadId);
        } catch (ClientException e) {
            e.printStackTrace();
            promise.reject(e);
        } catch (ServiceException e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    /**
     * multipartUpload
     * @param context
     * @param bucketName
     * @param objectKey
     * @param uploadId
     * @param filepath
     * @param options
     * @param promise
     */
    public void multipartUpload(final ReactContext context,String bucketName, String objectKey, String uploadId,String filepath, ReadableMap options,final Promise promise) {

        Uri selectedVideoUri = Uri.parse(filepath);
        // 1. content uri -> file path
        // 2. inputstream -> temp file path
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getCurrentActivity().getContentResolver().query(selectedVideoUri, proj, null, null, null);
            if (cursor == null) filepath = selectedVideoUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filepath = cursor.getString(column_index);
        } catch (Exception e) {
            filepath = FileUtils.getFilePathFromURI(context.getCurrentActivity(), selectedVideoUri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        long partSize = options.getInt("partSize"); // 设置分片大小
        int currentIndex = 1; // 上传分片编号，从1开始
        File uploadFile = new File(filepath); // 需要分片上传的文件
        InputStream input = null;
        try {
            input = new FileInputStream(uploadFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long fileLength = uploadFile.length();
        long uploadedLength = 0;
        List<PartETag> partETags = new ArrayList<PartETag>(); // 保存分片上传的结果
        while (uploadedLength < fileLength) {
            int partLength = (int)Math.min(partSize, fileLength - uploadedLength);
            byte[] partData = new byte[0]; // 按照分片大小读取文件的一段内容
            try {
                partData = IOUtils.readStreamAsBytesArray(input, partLength);
            } catch (IOException e) {
                e.printStackTrace();
                promise.reject(e);
            }
            UploadPartRequest uploadPart = new UploadPartRequest(bucketName, objectKey, uploadId, currentIndex);
            uploadPart.setPartContent(partData); // 设置分片内容
            UploadPartResult uploadPartResult = null;
            try {
                uploadPartResult = mOSS.uploadPart(uploadPart);
            } catch (ClientException e) {
                e.printStackTrace();
                promise.reject(e);
            } catch (ServiceException e) {
                e.printStackTrace();
                promise.reject(e);
            }
            partETags.add(new PartETag(currentIndex, uploadPartResult.getETag())); // 保存分片上传成功后的结果
            uploadedLength += partLength;
            currentIndex++;
        }

        CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(bucketName, objectKey,uploadId,partETags);
        CompleteMultipartUploadResult completeResult = null;
        try {
            completeResult = mOSS.completeMultipartUpload(complete);
            promise.resolve("mulitpartlaod success!");
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        complete.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", "<server address>");
                put("callbackBody", "<test>");
            }
        });
    }

    /**
     * abortMultipartUpload
     * @param bucketName
     * @param objectKey
     * @param uploadId
     * @param promise
     */
    public void abortMultipartUpload(String bucketName,String objectKey,String uploadId,final Promise promise) {
        AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);
        try {
            mOSS.abortMultipartUpload(abort);
            promise.resolve("abort multipart upload success!");
        } catch (ClientException e) {
            e.printStackTrace();
            promise.reject(e);
        } catch (ServiceException e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    /**
     * listParts
     * @param bucketName
     * @param objectKey
     * @param uploadId
     * @param promise
     */
    public void listParts (String bucketName,String objectKey,String uploadId,final Promise promise) {
        ListPartsRequest listParts = new ListPartsRequest(bucketName, objectKey, uploadId);
        ListPartsResult result = null;
        try {
            result = mOSS.listParts(listParts);
        } catch (ClientException e) {
            e.printStackTrace();
            promise.reject(e);
        } catch (ServiceException e) {
            e.printStackTrace();
            promise.reject(e);
        }

        WritableMap listPartsData = Arguments.createMap();

        for (int i = 0; i < result.getParts().size(); i++) {
            Log.d("listParts", "partNum: " + result.getParts().get(i).getPartNumber());
            Log.d("listParts", "partEtag: " + result.getParts().get(i).getETag());
            Log.d("listParts", "lastModified: " + result.getParts().get(i).getLastModified());
            Log.d("listParts", "partSize: " + result.getParts().get(i).getSize());
            listPartsData.putInt("partNum" + i, result.getParts().get(i).getPartNumber());
            listPartsData.putString("partEtag"+i,result.getParts().get(i).getETag());
//          listPartsData.("lastModified" + i,result.getParts().get(i).getLastModified());
            listPartsData.putDouble("partSize"+i,result.getParts().get(i).getSize());
        }
        promise.resolve(listPartsData);
    }
}
