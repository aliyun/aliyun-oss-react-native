//
//  RNAliyunOSS+UPLOAD.m
//  aliyun-oss-rn-sdk
//  Created by 罗章 on 2018/5/8.

#import "RNAliyunOSS+UPLOAD.h"

@implementation RNAliyunOSS (UPLOAD)

/**
 Asynchronous uploading
 */
RCT_REMAP_METHOD(asyncUpload, asyncUploadWithBucketName:(NSString *)bucketName objectKey:(NSString *)objectKey filepath:(NSString *)filepath options:(NSDictionary*)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    
    [self beginUploadingWithFilepath:filepath resultBlock:^(NSData *data) {
        OSSPutObjectRequest *put = [OSSPutObjectRequest new];
        //required fields
        put.bucketName = bucketName;
        put.objectKey = objectKey;
        put.uploadingData = data;
        
        // 设置Content-Type，可选
        //        put.contentType = @"application/octet-stream";
        //        // 设置MD5校验，可选
        //        put.contentMd5 = [OSSUtil base64Md5ForFilePath:@"<filePath>"]; // 如果是文件路径
        
        //optional fields
        put.uploadProgress = ^(int64_t bytesSent, int64_t totalByteSent, int64_t totalBytesExpectedToSend) {
            NSLog(@"%lld, %lld, %lld", bytesSent, totalByteSent, totalBytesExpectedToSend);
            
            // Only send events if anyone is listening
            if (self.hasListeners) {
                [self sendEventWithName:@"uploadProgress" body:@{@"bytesSent":[NSString stringWithFormat:@"%lld",bytesSent],
                                                                 @"currentSize": [NSString stringWithFormat:@"%lld",totalByteSent],
                                                                 @"totalSize": [NSString stringWithFormat:@"%lld",totalBytesExpectedToSend]}];
            }
        };
        
        OSSTask *putTask = [self.client putObject:put];
        
        [putTask continueWithBlock:^id(OSSTask *task) {
            
            if (!task.error) {
                NSLog(@"upload object success!");
                resolve(task.description);
            } else {
                NSLog(@"upload object failed, error: %@" , task.error);
                reject(@"Error", @"Upload failed", task.error);
            }
            return nil;
        }];
        
    }];
}
@end
