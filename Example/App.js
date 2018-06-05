import React, { Component } from 'react';

import {
  StyleSheet,
  View,
  ScrollView,
} from 'react-native';

import { AuthManager } from './Component/AuthManager'
import { UploadManager } from './Component/UploadManager'
import { DownloadManager } from './Component/DownloadManager'
import { ImageProcessManager } from './Component/ImageProcessManager'
import { BucketManager } from './Component/BucketManager'
import { ObjectManager } from './Component/ObjectManager'

import AliyunOSS from 'aliyun-oss-rn-sdk'
//open log 
AliyunOSS.enableDevMode()
// defalut configraiton
const configuration = {
   maxRetryCount: 3,  
   timeoutIntervalForRequest: 30,
   timeoutIntervalForResource: 24 * 60 * 60
};
const config = {
  AccessKey: 'XXX',
  SecretKey: 'XXX',
};
const endPoint = 'oss-cn-zhangjiakou.aliyuncs.com';
const companyserver = "http://XXX:PORT";
const familyserver = "http://XXX:PORT";

// AliyunOSS.initWithPlainTextAccessKey(config.AccessKey, config.SecretKey, endPoint, configuration);
AliyunOSS.initWithServerSTS(familyserver,endPoint, configuration)

type Props = {};

export default class App extends Component<Props> {
  render() {
    return (
      <ScrollView>
        <View style={styles.container}>
          <AuthManager/>
          <UploadManager/>
          <DownloadManager/>
          <ImageProcessManager/>
          <BucketManager/>
          <ObjectManager/>
        </View>
     </ScrollView>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flexDirection:'column',
    backgroundColor: '#F5FCFF',
    flexWrap:'wrap'
  }
});
