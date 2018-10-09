# Alibaba Cloud OSS SDK for React Native

## [README of Chinese](https://github.com/aliyun/aliyun-oss-react-native/blob/master/README-CN.md)

## Introduction

This document mainly describes how to install and use the OSS React Native SDK. This document assumes that you have already activated the Alibaba Cloud OSS service and created an *AccessKeyID* and an *AccessKeySecret*. In the document, *ID* refers to the *AccessKeyID* and *KEY* indicates the *AccessKeySecret*. If you have not yet activated or do not know about the OSS service, log on to the [OSS Product Homepage](http://www.aliyun.com/product/oss) for more help.

## Lanaguage

* JavaScript、JAVA、Objective-C

## Environment requirements

- Android ***2.3*** or above
- IOS ***8.0*** or above
- You must have registered an Alibaba Cloud account with the OSS activated.
- Node ***8.0*** or above
- React Native ***0.44.0*** or above

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API](#api)
- [DEMO](#DEMO)
- [FAQ](#FAQ)
- [JOIN](#join)
- [License](#license)
- [CONTACT](#contact)
- [Future](#Future)
- [Documentation](#Documentaion)

## Installation

NOTE: THIS PACKAGE IS NOW BUILT FOR REACT NATIVE 0.40 OR GREATER
* npm

```
npm install aliyun-oss-react-native --save
```
* yarn

```
yarn install aliyun-oss-react-native --save
```

### Automatic Installation

run `react-native link` in the react native project

```
react-native link aliyun-oss-react-native
```

`Note`：for suppport IPv6-Only networkd，you need to require :
```javascript
1. libresolv.tbd
2. CoreTelephony.framework
3. SystemConfiguration.framework
```

### Manual Installation

#### iOS

- **CocoaPods**

```
pod 'aliyun-oss-react-native', :path => '../node_modules/aliyun-oss-react-native'
````

- **no CocoaPods**

1. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>` Go to `node_modules` ➜ `aliyun-oss-react-native` ➜ `ios` ➜ select `RNAliyunOSS.xcodeproj`
2. Add `libRNAliyunOSS.a` to `Build Phases -> Link Binary With Libraries`
3. In XCode, in the project navigator, right click `Frameworks` ➜ `Add Files to [your project's name]`. Go to `node_modules` ➜ `aliyun-oss-react-native` ➜ `AliyunSDK`. Add `AliyunOSSiOS.framework`, and select *Copy items if needed* in the pop-up box.

#### Android
1. Add the following lines to `android/settings.gradle`:

```gradle
include ':react-native-react-sdk'
project(':react-native-react-sdk').projectDir = new File(rootProject.projectDir, '../node_modules/aliyun-oss-rn-sdk/android')
```

2. Add the compile line to the dependencies in `android/app/build.gradle`:
```gradle
dependencies {
  compile project(':aliyun-oss-react-native')
}
```
3. Add the required permissions in `AndroidManifest.xml`:

  ```xml
     <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     <uses-permission android:name="android.permission.CAMERA" />
  ```
4. Add the import and link the package in `MainApplication.java`:

```java
import com.reactlibrary.RNAliyunOssPackage;

 public class MainApplication extends Application implements ReactApplication {
  @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new RNAliyunOssPackage()
    );
  }
}
```

## Usage

Now ,all the API returns Promise Object exception init OSS Client API and enableDevMode API,so you can use  ES6 `async await ` or
`promise().then(/**/).catch(/**/)`,we take asyncUpload interface as an example.

* step-1:import AliyunOSS
```
import AliyunOSS from 'aliyun-oss-react-native'
```
* step-2:open debug mode  (optional)

```
AliyunOSS.enableDevMode();
```

* step-3:init configuration（optional）

```javascript
const configuration = {
   maxRetryCount: 3,
   timeoutIntervalForRequest: 30,
   timeoutIntervalForResource: 24 * 60 * 60
};
```

* step-4:init OSS Client，we provide 4 ways to init OSS Client. here we recommend initWithServerSTS

```javascript
const endpoint = "xxxx.aliyuncs.com"

AliyunOSS.initWithServerSTS("/***http://ip:port/****/",endPoint, configuration)
```
Note：We provide auth server with node shell in Script folder,you can run command link this.

1. `npm istall`
2. modify accessKeyId and accessKeySecret in the config.js
3. node index.js,port is defalut 9000，The  auth server is like (http|https)://ip.com:9000/

*  step-5:

```javascript
    <!-- note：filepath must start with file:/// -->
    AliyunOSS.asyncUpload(bucketname, objectkey, filePath).then( (res) => {
        <!-- you can see the log with React Native debugg tools or XCode 、Android studio console -->
        console.log(res)
    }).catch((error)=>{
        console.log(error)
    })
    <!-- addEventlistener uploadPross-->
   const downloadProgress = p => console.log(p.currentSize / p.totalSize);
   AliyuOSS.addEventListener('uploadProgress', downloadProgress);
```

## api

This section describes the APIs that are currently implemented and partially supported by the React Native SDK. These APIs mainly cover log management, bucket management, object management, authorization, file upload, and download. Follow-up to improve the relevant API and BUG repair. API list is as follows


API | Android | iOS
----| ---- | ---- |
enableDevMode|Y|Y|
initWithPlainTextAccessKey  |Y|Y
initWithSigner  |Y|Y
initWithSecurityToken   |Y|Y
initWithServerSTS   |Y|Y
asyncUpload   |Y| Y
initMultipartUpload |Y|Y
multipartUpload  | Y | Y
listParts |Y|Y
abortMultipartUpload |Y|Y
asyncDownload  |Y|Y
asyncCreateBucket  |Y|Y
asyncGetBucketACL  |Y|Y
asyncListBuckets  |Y|Y
asyncDeleteBucket  |Y|Y
asyncHeadObject  |Y|Y
asyncListObjects |Y|Y
doesObjectExist |Y|Y
doesObjectExist |Y|Y
asyncDeleteObject  |Y|Y


### enableDevMode

open dev log,please refer to the code

```
AliyunOSS.enableDevMode()
```

### initWithPlainTextAccessKey

init auth client with accessKeyId and accessKeySecret,please refer to the code.you can use ,but we do not suggest use it。

```javascript
const endPoint = "XXX"
const configuration = {
    maxRetryCount: 3,
    timeoutIntervalForRequest: 30,
    timeoutIntervalForResource: 24 * 60 * 60
 };
AliyunOSS.initWithPlainTextAccessKey(accessKey, secretKey, endPoint, configuration);
```

### initWithSigner

init auth client the sign

```javascript
AliyunOSS.initWithSigner(signature, accessKey, endPoint, configuration);
```

### initWithSecurityToken

init client with SecurityToken

AliyunOSS.initWithSecurityToken(SecurityToken, accessKeyId, accessKeySecret, endPoint, configuration);

```

### initWithServerSTS

init auth client with local auth server

```javascript
AliyunOSS.initWithServerSTS(/*local auth server*/, endPoint, configuration);
```

### asyncUpload

```
AliyunOSS.asyncUpload(bucketname, objectKey, filepath).then().catch()
```
### asyncAppendObject
### asyncResumableUpload
### initMultipartUpload

```javascript
 AliyunOSS.initMultipartUpload(bucketname,objectkey).then((e) => {
      //e is uploadId
      console.log(e)
    }).catch((error) => {
      console.log(error)
 })
```
### multipartUpload

```
//uploadId is  the value When call initMultipartUpload ,success callback return
AliyunOSS.multipartUpload(multipartBucket,mulitipartObjectKey,uploadId,filepath).then((res)=>{
    Alert.alert("success");
  }).catch((e) => {
    Alert.alert("fail");
  })
```
### listParts

```
AliyunOSS.listParts(multipartBucket,multipartObjectKey,upoadId).then((e)=>{
    Alert.alert("onListParts"+e)
  }).catch((e)=>{
    Alert.alert("onListPartsError")
 })
```
### abortMultipartUpload

```
 AliyunOSS.abortMultipartUpload(multipartBucket,multipartBucket,uploadId).then((e)=>{
    Alert.alert("abort success");
  }).catch((e)=>{
    Alert.alert("abort fali");
  })
```

### asyncDownload

```
 // xxx is the image process option
 AliyunOSS.asyncDownload(bucketname,objectkey,{"x-oss-process":'xxxx'}).then((e) => {
    console.log(e)
  }).catch((e)=>{
    console.log(e)
  })
```
### asyncCreateBucket

```
 AliyunOSS.asyncCreateBucket('tyluoluo','private','oss-cn-zhangjiakou').then((e) => {
    console.log(e)
  }).catch((e)=>{
     console.log(e)
  })
```

### asyncGetBucketACL

```javascript
 AliyunOSS.asyncGetBucketACL('luozhang002').then((e) => {
    console.log(e)
  }).catch((e)=>{
    console.log(e)
  })
```
### asyncListBuckets

```
AliyunOSS.asyncListBuckets().then((e) => {
    console.log(e)
  }).catch((e) => {
    console.log(e)
  })
```
### asyncDeleteBucket

```
 AliyunOSS.asyncDeleteBucket("tyluoluo").then((e) => {
    console.log(e)
  }).catch((e) => {
    console.log(e)
  })
```
### asyncHeadObject

```
 AliyunOSS.asyncHeadObject('luozhang002','yanxing').then((e)=>{
    console.log(e)
  }).catch((e)=> {
     console.log(e)
 })
```
### asyncListObjects

list objects in some conditions

parameters:

- name {String} bucket name
- options {Object}
  - [delimiter] {String} 
  - [prefix] {String} search buckets using `prefix` key
  - [marker] {String} search start from `marker`, including `marker` key
  - [max-keys] {String|Number} max buckets, default is `100`, limit to `1000` 
```
 AliyunOSS.asyncListObjects('luozhang002', {
    prefix:'xxxx'
}).then((e)=>{
    console.log(e)
  }).catch((e)=>{
     console.log(e)
  })
```
### doesObjectExist

```javascript
 AliyunOSS.doesObjectExist('luozhang001','xx.png').then( (e) => {
    console.log(e)
  }).catch((e) => {
    console.log(e)
  })
```

### asyncCopyObject

```javascript
 AliyunOSS.asyncCopyObject('luozhang001',"2.png","luozhang002","sbsbsb").then( (e) => {
    console.log(e)
  }).catch((e)=>{
    console.log("xxxx")
    console.log(e)
  })
```
### asyncDeleteObject

```javascript
 AliyunOSS.asyncDeleteObject('luozhang001','2.png').then((e)=>{
    Alert.alert(e)
  }).catch((e)=>{
    console.log(e)
  })
```

## DEMO

In the repository, we prodive RN SDK DEMO in the Example folder including andriod and ios,`Example/android` provide the android demo;`Example/iOS` provide the ios demo.Welcome to join us, how to run the Example ?

* step-1:clone the project and install some dependencies

```
1. git clone https://github.com/aliyun/aliyun-oss-react-native.git
2. cd Example
3. npm install
```

* step-2:start local auth server and modify the URL in initWithServerSTS function of Example/App.js,Server address must begin with ip,of cource you can refer to the scrpts folder we provied.defalut port is 9000

```
1. cd script/sts-app-server-node
2. node index.js
```

* step-3:run the project

1. npm run start
2. open Example/android ,compile and run with Android Studio.The effect is as follows

<image  text-align="center" width="200" height="400" src="https://img.alicdn.com/tfs/TB1nQ2pqSBYBeNjy0FeXXbnmFXa-658-1230.png"/>

3. open Example/NativeAddRN ,compile and run with XCode，The effect is as follows

<image width="200" height="400" src="https://img.alicdn.com/tfs/TB1ejWwqH9YBuNjy0FgXXcxcXXa-778-1488.png"/>

## F&Q

* Due to the complexity of React Native's own environment on the iOS side,it ofen occur differrent errors, please go to [stackoverflow](https://stackoverflow.com/questions/tagged/react-native)
* close proxy
* Sometimes,you can not see the ui in the debugging mode of the Android Studio Envrionment,  please delete the Build folder under the project file and then recompile

## Join

Now, React Native SDK is in the initial stage. When the developers use React Native API, any problem can be raised to the official warehouse for issue or PR. We will deal with it in time. Wecome much more developers to join us to serve customers and developers that who use aliyun storage services
To better serve customers and developers who use aliyun storage services.You can refer to the following documentation.

* React Native Android Native Modules：http://facebook.github.io/react-native/docs/native-modules-android.html
* React Native IOS Native Modules：http://facebook.github.io/react-native/docs/native-modules-ios.html
* React Native Debugging: http://facebook.github.io/react-native/docs/debugging.html
* Aliyun OSS Android SDK: https://github.com/aliyun/aliyun-oss-android-sdk
* Aliyun OSS iOS SDK: https://github.com/aliyun/aliyun-oss-ios-sdk

## License

* MIT

## Contact us

* [Alibaba Cloud OSS official website](http://oss.aliyun.com).
* [Alibaba Cloud OSS official forum](http://bbs.aliyun.com).
* [Alibaba Cloud OSS official documentation center](http://www.aliyun.com/product/oss#Docs).
* Alibaba Cloud official technical support: [Submit a ticket](https://workorder.console.aliyun.com/#/ticket/createIndex).

## Future

in the future, Continuously optimizing code documentation、add interfaces 、fix bugs, etc.

## Documentation

enhancement