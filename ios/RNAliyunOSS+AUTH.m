//
//  RNAliyunOSS+AUTH.m
//  aliyun-oss-rn-sdk
//
//  Created by 罗章 on 2018/5/8.
//

#import "RNAliyunOSS+AUTH.h"

@implementation RNAliyunOSS (AUTH)



/**
 initWithPlainTextAccessKey
 
 */
RCT_EXPORT_METHOD(initWithPlainTextAccessKey:(NSString *)accessKey secretKey:(NSString *)secretKey endPoint:(NSString *)endPoint configuration:(NSDictionary *)configuration){
    
    id<OSSCredentialProvider> credential = [[OSSPlainTextAKSKPairCredentialProvider alloc] initWithPlainTextAccessKey:accessKey secretKey:secretKey];
    
    [self initConfiguration: configuration];
    
    self.client = [[OSSClient alloc] initWithEndpoint:endPoint credentialProvider:credential clientConfiguration:self.clientConfiguration];
}


/**
 initWithImplementedSigner
 
 */
RCT_EXPORT_METHOD(initWithImplementedSigner:(NSString *)signature accessKey:(NSString *)accessKey endPoint:(NSString *)endPoint configuration:(NSDictionary *)configuration){
    
    id<OSSCredentialProvider> credential = [[OSSCustomSignerCredentialProvider alloc] initWithImplementedSigner:^NSString *(NSString *contentToSign, NSError *__autoreleasing *error) {
        if (signature != nil) {
            *error = nil;
        } else {
            // construct error object
            *error = [NSError errorWithDomain:endPoint code:OSSClientErrorCodeSignFailed userInfo:nil];
            return nil;
        }
        return [NSString stringWithFormat:@"OSS %@:%@", accessKey, signature];
    }];
    
    [self initConfiguration: configuration];
    
    self.client = [[OSSClient alloc] initWithEndpoint:endPoint credentialProvider:credential clientConfiguration:self.clientConfiguration];
}


/**
 initWithSecurityToken
 
 */
RCT_EXPORT_METHOD(initWithSecurityToken:(NSString *)securityToken accessKey:(NSString *)accessKey secretKey:(NSString *)secretKey endPoint:(NSString *)endPoint configuration:(NSDictionary *)configuration){
    
    id<OSSCredentialProvider> credential = [[OSSStsTokenCredentialProvider alloc] initWithAccessKeyId:accessKey secretKeyId:secretKey securityToken:securityToken];
    
    [self initConfiguration: configuration];
    
    
    self.client = [[OSSClient alloc] initWithEndpoint:endPoint credentialProvider:credential clientConfiguration:self.clientConfiguration];
}

/**
 initWithServerSTS
 */
RCT_EXPORT_METHOD(initWithServerSTS:(NSString *)server endPoint:(NSString *)endPoint configuration:(NSDictionary *)configuration){
    //直接访问鉴权服务器（推荐，token过期后可以自动更新）
    id<OSSCredentialProvider> credential = [[OSSAuthCredentialProvider alloc] initWithAuthServerUrl:server];
    
    [self initConfiguration: configuration];

    self.client = [[OSSClient alloc] initWithEndpoint:endPoint credentialProvider:credential clientConfiguration:self.clientConfiguration];
}


@end
