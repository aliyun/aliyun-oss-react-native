//
//  RNAliyunOSS+BUCKET.m
//  aliyun-oss-rn-sdk
//
//  Created by 罗章 on 2018/5/8.
//

#import "RNAliyunOSS+BUCKET.h"

@implementation RNAliyunOSS (BUCKET)

/**
 *异步创建bucket
 */

RCT_REMAP_METHOD(asyncCreateBucket,bucketName:(NSString*)bucketName acl:(NSString*)acl region:(NSString*)region resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSCreateBucketRequest * create = [OSSCreateBucketRequest new];
    create.bucketName = bucketName ;
    create.xOssACL = acl;
    create.location = region;
    OSSTask * createTask = [self.client createBucket:create];
    [createTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            NSLog(@"create bucket success!");
            resolve(@"create bucket success!");
        } else {
            NSLog(@"create bucket failed, error: %@", task.error);
            reject(@"Error", @"Upload failed", task.error);
        }
        return nil;
    }];
}


/*
 罗列出所有的bucket
 */
RCT_REMAP_METHOD(asyncListBuckets, resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSGetServiceRequest * getService = [OSSGetServiceRequest new];
    OSSTask * getServiceTask = [self.client getService:getService];
    [getServiceTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            OSSGetServiceResult * result = task.result;
            NSLog(@"buckets: %@", result.buckets);
            resolve(result.buckets);
            NSLog(@"owner: %@, %@", result.ownerId, result.ownerDispName);
            [result.buckets enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                NSDictionary * bucketInfo = obj;
                NSLog(@"BucketName: %@", [bucketInfo objectForKey:@"Name"]);
                NSLog(@"CreationDate: %@", [bucketInfo objectForKey:@"CreationDate"]);
                NSLog(@"Location: %@", [bucketInfo objectForKey:@"Location"]);
            }];
        }
        return nil;
    }];
}

/**
 asyncGetBucketACL
 */
RCT_REMAP_METHOD(asyncGetBucketACL, bucketName:(NSString*)bucketName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    OSSGetBucketACLRequest *getBucketACL = [OSSGetBucketACLRequest new];
    getBucketACL.bucketName = bucketName;
    
    OSSTask * osstask = [self.client getBucketACL:getBucketACL];
    [osstask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            OSSGetBucketACLResult * result = task.result;
            resolve(result.aclGranted);
        }else {
            reject(@"Error",@"getBucketACL fail",task.error);
        }
        return nil;
    }];
}

/*
 asyncDeleteBucket
 */
RCT_REMAP_METHOD(asyncDeleteBucket,withBucketName:(NSString*)bucketName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    OSSDeleteBucketRequest * delete = [OSSDeleteBucketRequest new];
    delete.bucketName = bucketName;
    OSSTask * deleteTask = [self.client deleteBucket:delete];
    [deleteTask continueWithBlock:^id(OSSTask *task) {
        if (!task.error) {
            NSLog(@"delete bucket success!");
            resolve(@"delete bucket success");
        } else {
            NSLog(@"delete bucket failed, error: %@", task.error);
            reject(@"Error", @"delete bucket failed", task.error);
        }
        return nil;
    }];
}


@end
