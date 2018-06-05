
/**
 * RNAliyunOSS.m refactor comments
 */

#import "RNAliyunOSS.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
@import Photos;
@import MobileCoreServices;


@implementation RNAliyunOSS

/**
 Will be called when this module's first listener is added.
 
 */
-(void)startObserving {
    _hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}


/**Will be called when this module's last listener is removed, or on dealloc.
 
 */
-(void)stopObserving {
    _hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}


/**
 Supported two events: uploadProgress, downloadProgress
 
 @return an array stored all supported events
 */
-(NSArray<NSString *> *)supportedEvents
{
    return @[@"uploadProgress", @"downloadProgress"];
}


/**
 Get local directory with read/write accessed
 
 @return document directory
 */
-(NSString *)getDocumentDirectory {
    NSString * path = NSHomeDirectory();
    NSLog(@"NSHomeDirectory:%@",path);
    NSString * userName = NSUserName();
    NSString * rootPath = NSHomeDirectoryForUser(userName);
    NSLog(@"NSHomeDirectoryForUser:%@",rootPath);
    NSArray * paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString * documentsDirectory = [paths objectAtIndex:0];
    return documentsDirectory;
}


/**
 Get a temporary directory inside of application's sandbox
 
 @return document directory
 */
-(NSString*)getTemporaryDirectory {
    NSString *TMP_DIRECTORY = @"react-native/";
    NSString *filepath = [NSTemporaryDirectory() stringByAppendingString:TMP_DIRECTORY];
    
    BOOL isDir;
    BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:filepath isDirectory:&isDir];
    if (!exists) {
        [[NSFileManager defaultManager] createDirectoryAtPath: filepath
                                  withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    return filepath;
}


/**
 Setup initial configuration for initializing OSS Client
 
 @param configuration a configuration object (NSDictionary *) passed from react-native side
 */
-(void)initConfiguration:(NSDictionary *)configuration {
    _clientConfiguration = [OSSClientConfiguration new];
    _clientConfiguration.maxRetryCount = [RCTConvert int:configuration[@"maxRetryCount"]]; //default 3
    _clientConfiguration.timeoutIntervalForRequest = [RCTConvert double:configuration[@"timeoutIntervalForRequest"]]; //default 30
    _clientConfiguration.timeoutIntervalForResource = [RCTConvert double:configuration[@"timeoutIntervalForResource"]]; //default 24 * 60 * 60
}


/**
 Begin a new uploading task
 Currently, support AssetLibrary, PhotoKit, and pure File for uploading
 Also, will convert the HEIC image to JPEG format
 
 @param filepath passed from reacit-native side, it might be a path started with 'assets-library://', 'localIdentifier://', 'file:'
 @param callback a block waiting to be called right after the binary data of asset is found
 */
-(void)beginUploadingWithFilepath:(NSString *)filepath resultBlock:(void (^) (NSData *))callback {
    
    // read asset data from filepath
    if ([filepath hasPrefix:@"assets-library://"]) {
        PHAsset *asset = [PHAsset fetchAssetsWithALAssetURLs:@[filepath] options:nil].firstObject;
        [self convertToNSDataFromAsset:asset withHandler:callback];
        
    } else if ([filepath hasPrefix:@"localIdentifier://"]) {
        NSString *localIdentifier = [filepath stringByReplacingOccurrencesOfString:@"localIdentifier://" withString:@""];
        PHAsset *asset = [PHAsset fetchAssetsWithLocalIdentifiers:@[localIdentifier] options:nil].firstObject;
        [self convertToNSDataFromAsset:asset withHandler:callback];
        
    } else {
        filepath = [filepath stringByReplacingOccurrencesOfString:@"file://" withString:@""];
        NSData *data = [NSData dataWithContentsOfFile:filepath];
        callback(data);
    }
}

/**
 a helper method to do the file convertion

 @param asset PHAsset
 @param handler a callback block
 */
-(void)convertToNSDataFromAsset:(PHAsset *)asset withHandler:(void (^) (NSData *))handler
{
    PHImageManager *imageManager = [PHImageManager defaultManager];
    
    switch (asset.mediaType) {
            
        case PHAssetMediaTypeImage: {
            PHImageRequestOptions *options = [[PHImageRequestOptions alloc] init];
            options.networkAccessAllowed = YES;
            [imageManager requestImageDataForAsset:asset options:options resultHandler:^(NSData * _Nullable imageData, NSString * _Nullable dataUTI, UIImageOrientation orientation, NSDictionary * _Nullable info) {
                if ([dataUTI isEqualToString:(__bridge NSString *)kUTTypeJPEG]) {
                    handler(imageData);
                } else {
                    //if the image UTI is not JPEG, then do the convertion to make sure its compatibility
                    CGImageSourceRef source = CGImageSourceCreateWithData((__bridge CFDataRef)imageData, NULL);
                    NSDictionary *imageInfo = (__bridge NSDictionary*)CGImageSourceCopyPropertiesAtIndex(source, 0, NULL);
                    NSDictionary *metadata = [imageInfo copy];
                    
                    NSMutableData *imageDataJPEG = [NSMutableData data];
                    
                    CGImageDestinationRef destination = CGImageDestinationCreateWithData((__bridge CFMutableDataRef)imageDataJPEG, kUTTypeJPEG, 1, NULL);
                    CGImageDestinationAddImageFromSource(destination, source, 0, (__bridge CFDictionaryRef)metadata);
                    CGImageDestinationFinalize(destination);
                    
                    handler([NSData dataWithData:imageDataJPEG]);
                }
            }];
            break;
        }
            
        case PHAssetMediaTypeVideo:{
            PHVideoRequestOptions *options = [[PHVideoRequestOptions alloc] init];
            options.networkAccessAllowed = YES;
            [imageManager requestExportSessionForVideo:asset options:options exportPreset:AVAssetExportPresetHighestQuality resultHandler:^(AVAssetExportSession * _Nullable exportSession, NSDictionary * _Nullable info) {
                
                //generate a temporary directory for caching the video (MP4 Only)
                NSString *filePath = [[self getTemporaryDirectory] stringByAppendingString:[[NSUUID UUID] UUIDString]];
                filePath = [filePath stringByAppendingString:@".mp4"];
                
                exportSession.shouldOptimizeForNetworkUse = YES;
                exportSession.outputFileType = AVFileTypeMPEG4;
                exportSession.outputURL = [NSURL fileURLWithPath:filePath];
                
                [exportSession exportAsynchronouslyWithCompletionHandler:^{
                    handler([NSData dataWithContentsOfFile:filePath]);
                }];
            }];
            break;
        }
        default:
            break;
    }
}

/**
 Expose this native module to RN
 */
RCT_EXPORT_MODULE()

@end
