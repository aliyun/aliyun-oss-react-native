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

import AliyunOSS from 'aliyun-oss-react-native'

//open log 
AliyunOSS.enableDevMode()

// defalut configraiton
const configuration = {
   maxRetryCount: 3,  
   timeoutIntervalForRequest: 30,
   timeoutIntervalForResource: 24 * 60 * 60
};

const endPoint = 'oss-cn-XXX.aliyuncs.com';
const authserver = "http://XXX:PORT";

// initWithServerSTS to auth
AliyunOSS.initWithServerSTS(authserver,endPoint, configuration)

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
