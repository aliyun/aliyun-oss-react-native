//
//  RNAliyunOSS+LOG.m
//  aliyun-oss-rn-sdk
//
//  Created by 罗章 on 2018/5/8.
//

#import "RNAliyunOSS+LOG.h"

@implementation RNAliyunOSS (LOG)

/**
 enable the dev mode
 */
RCT_EXPORT_METHOD(enableDevMode){
    // enable OSS logger
    [OSSLog enableLog];
}
@end
