//
//  RNAliyunOSS+DOWNLOAD.m
//  aliyun-oss-rn-sdk
//
//  Created by 罗章 on 2018/5/8.
//

#import "RNAliyunOSS+DOWNLOAD.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
@implementation RNAliyunOSS (DOWNLOAD)


/**
 Asynchronous downloading
 
 */
RCT_REMAP_METHOD(asyncDownload, asyncDownloadWithBucketName:(NSString *)bucketName objectKey:(NSString *)objectKey filepath:(NSString *)filepath options:(NSDictionary*)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    
    OSSGetObjectRequest * get = [OSSGetObjectRequest new];
    
    //required fields
    get.bucketName = bucketName;
    get.objectKey = objectKey;
    
    //图片处理情况
    NSString *xOssProcess = [RCTConvert NSString:options[@"x-oss-process"]];
    get.xOssProcess = xOssProcess;
    
    //optional fields
    get.downloadProgress = ^(int64_t bytesWritten, int64_t totalBytesWritten, int64_t totalBytesExpectedToWrite) {
        NSLog(@"%lld, %lld, %lld", bytesWritten, totalBytesWritten, totalBytesExpectedToWrite);
        //         Only send events if anyone is listening
        if (self.hasListeners) {
            [self sendEventWithName:@"downloadProgress" body:@{@"bytesWritten":[NSString stringWithFormat:@"%lld",bytesWritten],
                                                               @"currentSize": [NSString stringWithFormat:@"%lld",totalBytesWritten],
                                                               @"totalSize": [NSString stringWithFormat:@"%lld",totalBytesExpectedToWrite]}];
        }
    };
    
    if (![[filepath oss_trim] isEqualToString:@""]) {
        get.downloadToFileURL = [NSURL fileURLWithPath:[filepath stringByAppendingPathComponent:objectKey]];
    } else {
        NSString *docDir = [self getDocumentDirectory];
        get.downloadToFileURL = [NSURL fileURLWithPath:[docDir stringByAppendingPathComponent:objectKey]];
    }
    
    OSSTask * getTask = [self.client getObject:get];
    
    [getTask continueWithBlock:^id(OSSTask *task) {
        
        if (!task.error) {
            NSLog(@"download object success!");
            OSSGetObjectResult *result = task.result;
            NSLog(@"download dota length: %lu", [result.downloadedData length]);
            resolve([get.downloadToFileURL absoluteString]);
        } else {
            NSLog(@"download object failed, error: %@" ,task.error);
            reject(@"Error", @"Download failed", task.error);
        }
        return nil;
    }];
}

@end
