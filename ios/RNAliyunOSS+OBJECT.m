//
//  RNAliyunOSS+OBJECT.m
//  aliyun-oss-rn-sdk
//
//  Created by 罗章 on 2018/5/8.
//

#import "RNAliyunOSS+OBJECT.h"

@implementation RNAliyunOSS (OBJECT)

/*
 asyncListObjects
 */

RCT_REMAP_METHOD(asyncListObjects, bucketName:(NSString*)bucketName options:(NSDictionary*)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSGetBucketRequest * getBucket = [OSSGetBucketRequest new];
    getBucket.bucketName = bucketName;
    // 可选参数，具体含义参考：https://docs.aliyun.com/#/pub/oss/api-reference/bucket&GetBucket
    // getBucket.marker = @"";
    // getBucket.delimiter = @"";
    
    if([options objectForKey:@"delimiter"]) {
        getBucket.delimiter = [options objectForKey:@"delimiter"];
    }
    
    if([options objectForKey:@"marker"]) {
        getBucket.delimiter = [options objectForKey:@"marker"];
    }
    
    if([options objectForKey:@"prefix"]) {
        getBucket.delimiter = [options objectForKey:@"prefix"];
    }
    
    if([options objectForKey:@"maxkeys"]) {
        getBucket.delimiter = [options objectForKey:@"maxkeys"];
    }
    
    OSSTask * getBucketTask = [self.client getBucket:getBucket];
    [getBucketTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            OSSGetBucketResult * result = task.result;
            resolve(result.contents);
        } else {
            NSLog(@"get bucket failed, error: %@", task.error);
            reject(@"Error",@"get bucket failed",task.error);
        }
        return nil;
    }];
}

/*
 * doesObjectExist
 */
RCT_REMAP_METHOD(doesObjectExist, bucketName:(NSString*)bucketName objectKey:(NSString*)objectKey resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject ) {
    NSError * error = nil;
    BOOL isExist = [self.client doesObjectExistInBucket:bucketName objectKey:objectKey error:&error];
    if (!error) {
        if(isExist) {
            NSLog(@"File exists.");
            resolve(@"File exists.");
        } else {
            NSLog(@"File not exists.");
            resolve(@"File not exists.");
        }
    } else {
        NSLog(@"Error!");
        reject(@"error",@"error",error);
    }
}

/*
 *asyncCopyObject
 */

RCT_REMAP_METHOD(asyncCopyObject, srcBucketName:(NSString*)srcBucketName srcObjectKey:(NSString*)srcObjectKey  desBucketName:(NSString*)desBucketName desObjectKey:(NSString*)desObjectKey options:(NSDictionary*)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject ) {
    OSSCopyObjectRequest * copy = [OSSCopyObjectRequest new];
    copy.bucketName = desBucketName;
    copy.objectKey = desObjectKey;
    copy.sourceCopyFrom = [NSString stringWithFormat:@"/%@/%@", srcBucketName, srcObjectKey];
    OSSTask * task = [self.client copyObject:copy];
    [task continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            resolve(@"copy success!");
            // ...
        } else {
            NSLog(@"coppy fail error");
            reject(@"Error",@"Copy fail",task.error);
        }
        return nil;
    }];
}

/*
 asyncHeadObject
 */
RCT_REMAP_METHOD(asyncHeadObject, withBucketName:(NSString*)bucketName withObjectKey:(NSString*)objectKey resolver: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSHeadObjectRequest * head = [OSSHeadObjectRequest new];
    head.bucketName = bucketName;
    head.objectKey = objectKey;
    OSSTask * headTask = [self.client headObject:head];
    [headTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            OSSHeadObjectResult * headResult = task.result;
            //            NSLog(@"all response header: %@", headResult.httpResponseHeaderFields);
            // some object properties include the 'x-oss-meta-*'s
            //            NSLog(@"head object result: %@", headResult.objectMeta);
            resolve(headResult.objectMeta);
        } else {
            NSLog(@"head object error: %@", task.error);
            reject(@"Error",@"head object error",task.error);
        }
        return nil;
    }];
}

/*
 astncDeleteObject
 */
RCT_REMAP_METHOD(asyncDeleteObject, bucketName:(NSString*)bucketName withObjectKey:(NSString*)objectKey resolver: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSDeleteObjectRequest * delete = [OSSDeleteObjectRequest new];
    delete.bucketName = bucketName;
    delete.objectKey = objectKey;
    OSSTask * deleteTask = [self.client deleteObject:delete];
    [deleteTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            resolve(@"delete object success");
        } else {
            reject(@"error",@"delete error",task.error);
        }
        return nil;
    }];
}

@end
