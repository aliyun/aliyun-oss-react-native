# Aliyun OSS SDK for React Native

## [README of English](https://github.com/aliyun/aliyun-oss-react-native/blob/master/README.md)

## 简介

本文档主要介绍OSS React Native SDK的安装和使用。本文档假设您已经开通了阿里云OSS 服务，并创建了Access Key ID 和Access Key Secret。文中的ID 指的是Access Key ID，KEY 指的是Access Key Secret。如果您还没有开通或者还不了解OSS，请登录[OSS产品主页](http://www.aliyun.com/product/oss)获取更多的帮助。

## 开发语言

* JavaScript 、JAVA、Objective-C

## 环境要求

- Android系统版本: 2.3及以上
- IOS系统版本: 8.0及以上
- 必须注册有Aliyun.com用户账户,并开通OSS服务。
- Node版本: 8.0及以上
- React Native版本：0.44.0及以上

## 内容

- [安装](#安装)
- [使用](#使用)
- [接口](#接口)
- [示例](#示例)
- [常见问题](#常见问题)
- [参与开源](#参与开源)
- [证书](#证书)
- [联系我们](#联系我们)
- [未来](#未来)
- [文档](#文档)

## 安装

注意: react-native版本建议0.44.0及以上，建议使用fackebook官方[react-native-cli](https://www.npmjs.com/package/react-native-cli)或者[create-react-native-app](https://www.npmjs.com/package/create-react-native-app)脚手架进行react native项目构建。aliyun-oss-react-native可通过npm或者yarn安装

* npm

```script
npm install aliyun-oss-react-native  --save
```

* yarn

```script
yarn install aliyun-oss-react-native --save
```
### 自动安装

react native项目下运行`react-native link`命令

```script
react-native link aliyun-oss-react-native
```

注意：由于react native脚手架不同版本的问题，有时候自动安装可能会失败，可手动添加android和iOS的的依赖库。同样为了兼容IPv6-Only网络，iOS工程中需参考aliyun-oss-ios-sdk说明中引入以下包。
```html
1. libresolv.tbd
2. CoreTelephony.framework
3. SystemConfiguration.framework
```

### 手动安装

#### iOS

- **CocoaPods**
```
pod 'aliyun-oss-react-native', :path => '../node_modules/aliyun-oss-react-native'
````

- **非CocoaPods**

1. 在XCode  Project navigator面板中, 右键单击工程Libraries文件 ➜ 选择`Add Files to <...>` 进入 `node_modules` ➜ `aliyun-oss-react-native` ➜ `ios` ➜ select `RNAliyunOSS.xcodeproj`
2. 在XCode  Project navigator面板中, 添加`RNAliyunOSS.a` to `Build Phases -> Link Binary With Libraries`
3. 在XCode  Project navigator面板中，右键单击[framework] ➜ Add Files to [your project's name]. 进入node_modules ➜ aliyun-oss-rn-sdk ➜ AliyunSDK. Add AliyunOSSiOS.framework

#### Android
1. `settings.gradle`
    ```gradle
    include ':aliyun-oss-react-native'
    project(':aliyun-oss-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/aliyun-oss-react-native/android')
    ```
2. `build.gradle`
    ```gradle
    dependencies {
        compile project(':aliyun-oss-react-native')
    }
    ```

3. `MainApplication.java`
    ```java
   import com.reactlibrary.RNAliyunOssPackage;

    public class MainApplication extends Application implements ReactApplication {
     @Override
       protected List<ReactPackage> getPackages() {
         return Arrays.<ReactPackage>asList(
             new MainReactPackage(),
               new ImagePickerPackage(),
               new RNAliyunOssPackage()
         );
       }
    }
    ```
## 使用

目前接口除了初始化客户端和开启日志功能接口外，其他的接口都返回Promise对象，开发者可以使用async await的语法，也可使用原生的
`promise().then(/**/).catch(/**/)`,接口的使用基本一样，以上传接口为例进行说明

* step-1:导入AliyunOSS
```
import AliyunOSS from 'aliyun-oss-react-native'
```
* step-2:开启调试模式 (可选)

```
AliyunOSS.enableDevMode();
```

* step-3:初始化配置选项（可选）
```javascript
const configuration = {
   maxRetryCount: 3,
   timeoutIntervalForRequest: 30,
   timeoutIntervalForResource: 24 * 60 * 60
};
```

* step-4:初始化OSS Client，目前提供了4中初始化OSS Client方式，这里调用initWithServerSTS，其他的几个接口可参考该文档中的API

```javascript
const endpoint = "xxxx.aliyuncs.com"

AliyunOSS.initWithServerSTS("/***http://ip:端口/****/",endPoint, configuration)
```
备注：仓库文件中提供Node脚步启用本地鉴权服务，打开script文件夹运行，运行以下命令：

1. `npm istall`
2. 修改config中accessKeyId 和 accessKeySecret
3. node index.js,端口默认9000，鉴权服务地址为:(http|https)://ip:9000/

*  step-5:

```javascript
  <!-- 备注：目前接口仅暴漏filePath,上传路径为file:/// -->
  AliyunOSS.asyncUpload(bucketname, objectkey, filePath).then( (res) => {
    <!-- log的查看可以通过React Native自带的调试工具也可通过XCode Log控制台进行查看 -->
    console.log(res)
  }).catch((error)=>{
    console.log(error)
  })
  <!-- 监听上传事件和上传进度-->
 const downloadProgress = p => console.log(p.currentSize / p.totalSize);
 AliyunOSS.addEventListener('uploadProgress', downloadProgress);
```

## 接口

主要介绍目前React Native SDK已经实现并部分支持的API，主要涉及到日志管理、Bucket管理、Object管理、授权、文件上传和下载等。后续持续完善相关API和BUG修复。API列表如下

API | Android | iOS
-------------- | ---- |---- |
enableDevMode |支持| 支持
initWithPlainTextAccessKey | 支持| 支持
initWithSigner | 支持| 支持
initWithSecurityToken | 支持| 支持
initWithServerSTS  | 支持| 支持
asyncUpload  | 支持| 支持 |
initMultipartUpload  | 支持| 支持
multipartUpload   | 支持| 支持
listParts   | 支持| 支持
abortMultipartUpload | 支持| 支持
asyncDownload  | 支持| 支持
asyncCreateBucket   | 支持| 支持
asyncGetBucketACL   | 支持| 支持
asyncListBuckets  | 支持 | 支持
asyncDeleteBucket  | 支持| 支持
asyncHeadObject  | 支持| 支持
asyncListObjects  | 支持| 支持
doesObjectExist  | 支持| 支持
asyncCopyObject |支持|支持
asyncDeleteObject | 支持| 支持

### enableDevMode

该接口主要是开启日志记录功能,具体使用参考：

```
AliyunOSS.enableDevMode()
```

### initWithPlainTextAccessKey(不建议)

该接口需要通过明文授权accesskeyId和accesskeySecret，开发者这可以使用，但是我们不建议

```javascript
const endPoint = "XXX"
const configuration = {
    maxRetryCount: 3,
    timeoutIntervalForRequest: 30,
    timeoutIntervalForResource: 24 * 60 * 60
 };
AliyunOSS.initWithPlainTextAccessKey(accesskeyId, accesskeySecret, endPoint, configuration);
```

### initWithSigner

该接口通过自签方式授权,请参考[自签名模式](https://help.aliyun.com/document_detail/32046.html?spm=a2c4g.11186623.2.12.W3Zm1U),使用可查看

```javascript
AliyunOSS.initWithSigner(signature, accessKey, endPoint, configuration);
```

### initWithSecurityToken

该接口通过SecurityToken授权，参考[STS访问控制](https://help.aliyun.com/document_detail/32046.html?spm=a2c4g.11186623.2.8.dfV9i0),使用可查看

```
AliyunOSS.initWithSecurityToken(SecurityToken, accessKeyId, accessKeySecret, endPoint, configuration);
```

### initWithServerSTS

该接口通过本地鉴权服务器授权，使用可查看

```javascript
AliyunOSS.initWithSecurityToken(/*local auth server*/, endPoint, configuration);
```

### asyncUpload

```
AliyunOSS.asyncUpload(bucketname, objectKey, filepath).then().catch()

```
### asyncAppendObject
### asyncResumableUpload
### initMultipartUpload

```javascript
 AliyunOSS.initMultipartUpload(bucketname,objectkey).then((e)=>{
       //e 为uploadId
       console.log(e)
    }).catch((error) => {
       console.log(error)
 })
```
### multipartUpload

```
//uploadId为initMultipartUpload成功回调后的返回值
AliyunOSS.multipartUpload(multipartBucket,mulitipartObjectKey,uploadId,filepath).then((res)=>{
    Alert.alert("分片上传成功");
  }).catch((e) => {
    Alert.alert("分片上传失败");
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
    Alert.alert("分片终止成功");
  }).catch((e)=>{
    Alert.alert("分片终止失败");
  })
```

### asyncDownload

```
 // xxx为图片处理选项，具体可查看官网
 AliyunOSS.asyncDownload(bucketname,objectkey,{"x-oss-process": 'xxxx'}).then((e) => {
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

列举指定bucket下的objects

parameters:

- name {String} bucket name
- options {Object}
  - [delimiter] {String} 用于对Object名字进行分组的字符。所有名字包含指定的前缀且第一次出现delimiter字符之间的object作为一组元素: CommonPrefixes。
  - [marker] {String} 设定结果从marker之后按字母排序的第一个开始返回。
  - [maxkeys] {Number} 限定此次返回object的最大数，如果不设定，默认为100，maxkeys取值不能大于1000。
  - [prefix]  {String} 限定返回的object key必须以prefix作为前缀。注意使用prefix查询时，返回的key中仍会包含prefix。

```
 AliyunOSS.asyncListObjects('luozhang002',{
    prefix:'xxxx'
  }).then((e)=>{
    console.log(e)
  }).catch((e)=>{
     console.log(e)
  })
```
### doesObjectExist

```
 AliyunOSS.doesObjectExist('luozhang001','xx.png').then((e)=>{
    console.log(e)
  }).catch((e)=>{
     console.log(e)
  })
```

### asyncCopyObject

```
 AliyunOSS.asyncCopyObject('luozhang001',"2.png","luozhang002","sbsbsb").then((e)=>{
      console.log(e)
    }).catch((e)=>{
      console.log("xxxx")
      console.log(e)
    })
```
### asyncDeleteObject

```
 AliyunOSS.asyncDeleteObject('luozhang001','2.png').then((e)=>{
     Alert.alert(e)
  }).catch((e)=>{
    console.log(e)
  })
```


## 示例

仓库Example中提供了React Native android和iOS端运行demo，Android为Example下的android工程，iOS为Example下的iOS工程，目前Example提供了所有已开发的API DEMO，还在完善中，欢迎参与共建。如何运行Example呢？

* step-1:克隆项目并安装依赖包

```
1. git clone https://github.com/aliyun/aliyun-oss-react-native.git
2. cd Example
3. npm install
```

* step-2:启动本地鉴权服务器并修改Example/App.js中initWithServerSTS服务地址，注意服务地址要以IP开口

```
1. cd script/sts-app-server-node
2. node index.js
```

* step-3:运行项目

1. npm run start
2. 安卓使用Android Studio打开工程Example/android编译并运行,效果如图

<image  text-align="center" width="200" height="400" src="https://img.alicdn.com/tfs/TB1nQ2pqSBYBeNjy0FeXXbnmFXa-658-1230.png"/>

3. ios使用XCode打开工程Example/NativeAddRN编译并运行，效果如图

<image width="200" height="400" src="https://img.alicdn.com/tfs/TB1ejWwqH9YBuNjy0FgXXcxcXXa-778-1488.png"/>

## 常见问题

* iOS端由于React Native自身环境的复杂性，会莫名奇妙的报各种编译错，请移步[stackoverflow](https://stackoverflow.com/questions/tagged/react-native),或者直接可运行项目Example目录下IOS工程NativeAddRN进行对比，如何运行Example请参考[示例](#示例)
* 关闭代理
* 针对Android Studio调试环境下看不到界面，请删除工程文件下的Build/文件夹重新编译

## 参与开源

目前React Native SDK处于起步阶段，开发者使用React Native API的过程中，遇到任何问题都可以向官方仓库提Issue或者PR，我们会第一时间进行处理。欢迎广大开发者参与共建，修复和完善更多的API
来更好的服务使用阿里云存储服务的客户和开发者。具体开发可参考如下文档

* React Native Android Native Modules：http://facebook.github.io/react-native/docs/native-modules-android.html
* React Native IOS Native Modules：http://facebook.github.io/react-native/docs/native-modules-ios.html
* React Native Debugging: http://facebook.github.io/react-native/docs/debugging.html
* 阿里云OSS安卓SDK：https://github.com/aliyun/aliyun-oss-android-sdk
* 阿里云OSS IOS SDK https://github.com/aliyun/aliyun-oss-ios-sdk

## License

* MIT

## 联系我们

* 阿里云OSS官方网站：http://oss.aliyun.com
* 阿里云OSS官方论坛：http://bbs.aliyun.com
* 阿里云OSS官方文档中心：http://www.aliyun.com/product/oss#Docs
* 阿里云官方技术支持 登录OSS控制台 https://home.console.aliyun.com -> 点击"工单系统"

## 未来

* 未来持续改进、优化代码文档、新增接口、修复Bug等

## 文档

* 后续完善