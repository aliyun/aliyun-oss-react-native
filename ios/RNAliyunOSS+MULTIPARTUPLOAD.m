//
//  RNAliyunOSS+MULTIPARTUPLOAD.m
//  aliyun-oss-rn-sdk
//  Created by 罗章 on 2018/5/10.

#import "RNAliyunOSS+MULTIPARTUPLOAD.h"

@implementation RNAliyunOSS (MULTIPARTUPLOAD)

/*
 *initMultipartUpload
 */
RCT_REMAP_METHOD(initMultipartUpload,withBucketName:(NSString *)bucketName objectKey:(NSString *)objectKey resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    /**init mulitpart*/
    __block NSString * uploadId = nil;
    NSString * uploadToBucket = bucketName;
    NSString * uploadObjectkey = objectKey;
    OSSInitMultipartUploadRequest * init = [OSSInitMultipartUploadRequest new];
    init.bucketName = uploadToBucket;
    init.objectKey = uploadObjectkey;
    // init.contentType = @"application/octet-stream";
    OSSTask * initTask = [self.client multipartUploadInit:init];
    [initTask waitUntilFinished];
    if (!initTask.error) {
        OSSInitMultipartUploadResult * result = initTask.result;
        uploadId = result.uploadId;
        NSLog(@"initMultipartLoad success!");
        resolve(uploadId);
    } else {
        NSLog(@"multipart upload failed, error: %@", initTask.error);
        reject(@"Error",@"multipart upload failed",initTask.error);
        return;
    }
}

/*
 multipartUpload
 */
RCT_REMAP_METHOD(multipartUpload, withBucketName:(NSString *)bucketName objectKey:(NSString *)objectKey uploadId:(NSString*)uploadId withFilePath:(NSString*)filePath  options:(NSDictionary*)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    __block NSMutableArray * partInfos = [NSMutableArray new];
    filePath = [NSHomeDirectory() stringByAppendingPathComponent:filePath];
    //分片上传数量
    int chuckCount = 2;
    //获取文件大小
    long fileSize;
    NSFileManager* manager =[NSFileManager defaultManager];
    if ([manager fileExistsAtPath:filePath]){
        fileSize = [[manager attributesOfItemAtPath:filePath error:nil]fileSize];
    }
    //分片大小
    uint64_t offset = fileSize/chuckCount;
    
    for (int i = 1; i <= chuckCount; i++) {
        OSSUploadPartRequest * uploadPart = [OSSUploadPartRequest new];
        uploadPart.bucketName = bucketName;
        uploadPart.objectkey = objectKey;
        uploadPart.uploadId = uploadId;
        uploadPart.partNumber = i; // part number start from 1
        NSFileHandle* readHandle = [NSFileHandle fileHandleForReadingAtPath:filePath];
        [readHandle seekToFileOffset:offset * (i -1)];
        NSData* data = [readHandle readDataOfLength:offset];
        uploadPart.uploadPartData = data;
        OSSTask * uploadPartTask = [self.client uploadPart:uploadPart];
        [uploadPartTask waitUntilFinished];
        if (!uploadPartTask.error) {
            OSSUploadPartResult * result = uploadPartTask.result;
            uint64_t fileSize = [[[NSFileManager defaultManager] attributesOfItemAtPath:uploadPart.uploadPartFileURL.absoluteString error:nil] fileSize];
            [partInfos addObject:[OSSPartInfo partInfoWithPartNum:i eTag:result.eTag size:fileSize]];
        } else {
            NSLog(@"upload part error: %@", uploadPartTask.error);
            return;
        }
    }
    //完成分片上传
    OSSCompleteMultipartUploadRequest * complete = [OSSCompleteMultipartUploadRequest new];
    complete.bucketName = bucketName;
    complete.objectKey = objectKey;
    complete.uploadId = uploadId;
    complete.partInfos = partInfos;
    OSSTask * completeTask = [self.client completeMultipartUpload:complete];
    [[completeTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            OSSCompleteMultipartUploadResult * result = task.result;
            NSLog(@"upload server success");
            resolve(@"upload server success");
        } else {
            NSLog(@"upload server failed",task.error);
            reject(@"Error",@"upload server failed",task.error);
        }
        return nil;
    }] waitUntilFinished];
}

/*
 * abortMultipartUpload
 */
RCT_REMAP_METHOD(abortMultipartUpload,withBucketName: (NSString*)bucketName objectKey: (NSString*)objectKey uploadId:(NSString*)uploadId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    OSSAbortMultipartUploadRequest * abort = [OSSAbortMultipartUploadRequest new];
    abort.bucketName = bucketName;
    abort.objectKey = objectKey;
    abort.uploadId = uploadId;
    OSSTask * abortTask = [self.client abortMultipartUpload:abort];
    [abortTask waitUntilFinished];
    if (!abortTask.error) {
        OSSAbortMultipartUploadResult * result = abortTask.result;
        //      uploadId = result.uploadId;
        NSLog(@"abort success!");
        resolve(@"abort success");
    } else {
        NSLog(@"multipart upload failed, error: %@", abortTask.error);
        reject(@"Error",@"multipart upload failed",abortTask.error);
        return;
    }
}

/*
 * list parts
 */
RCT_REMAP_METHOD(listParts, withBucketName:(NSString*)bucketName withObjectKey: (NSString*)objectKey uploadId:(NSString*)uploadId resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    
    OSSListPartsRequest * listParts = [OSSListPartsRequest new];
    listParts.bucketName = bucketName;
    listParts.objectKey = objectKey;
    listParts.uploadId =  uploadId;
    OSSTask * listPartTask = [self.client listParts:listParts];
    [listPartTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            NSLog(@"list part result success!");
            OSSListPartsResult * listPartResult = task.result;
            for (NSDictionary * partInfo in listPartResult.parts) {
                NSLog(@"each part: %@", partInfo);
            }
            resolve(@"listParst success");
        } else {
            NSLog(@"list part result error: %@", task.error);
            reject(@"Error",@"list part result error",task.error);
        }
        return nil;
    }];
}
@end
