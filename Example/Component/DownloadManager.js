import React, { Component } from 'react';

import {
  Platform,
  StyleSheet,
  Text,
  View,
  Alert,
  Button,
  ScrollView,
  Image,
  DeviceEventEmitter
} from 'react-native';

//导入样式
import { styles } from '../CSS/global.js' 

export class DownloadManager extends Component {
	render() {
		return (
    	<View style={styles.item}>
    	  <Text style={styles.description}>文件下载</Text>
    	  <View style={styles.detailitem}>
    	    <View style={styles.button}>
    	      <Button
    	        onPress={this.handClick}
    	        title="下载文件"
    	        color="#841584"
    	      />
    	    </View>        
    	  </View>
    	</View>
		)
	}
	handClick() {
    console.log("准备下载")
    AliyunOSS.asyncDownload('luozhang002','yanxing').then((e)=>{
      Alert.alert(e)
      console.log(e)
    }).catch((e)=>{
      console.log(e)
    })
  }

  componentDidMount(){
    const downloadProgress = p => console.log(p.currentSize / p.totalSize);
    AliyunOSS.addEventListener('downloadProgress', downloadProgress);
  }

}