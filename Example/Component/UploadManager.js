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

//保存初始化分片上传ID
let uploadId;
const multipartBucket = "luozhang002"
const mulitipartObject = "extremlyboy"
let ImagePicker = require('react-native-image-picker');

//导入样式
import { styles } from '../CSS/global.js' 

const  options = {
  title: 'Select Avatar',
  customButtons: [
    {name: 'fb', title: 'Choose Photo from Facebook'},
  ],
  storageOptions: {
    skipBackup: true,
    path: 'images'
  }
};

export class UploadManager extends Component {  
  render() {  
    return(
      <View style={styles.item}>
        <Text style={styles.description}>文件上传操作</Text>
        <Image source={require('../resource/putao.jpeg')}/>
        <View style={styles.detailitem}>
          
          <View style={styles.button}>
            <Button
              onPress={this.handleClick.bind(this,"uploadFile")}
              title="上传文件"
              color="#841584"
            />
          </View> 
        
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"appendObject")}
              title="追加文件"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"resumeObject")}
              title="断点续传"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"initMultipartUpload")}
              title="初始化分片"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"multipartUpload")}
              title="分片上传"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"abortMultipartUpload")}
              title="取消分片上传"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handleClick.bind(this,"listParts")}
              title="列出分片"
              color="#841584"
            />
          </View>
        </View>
      </View>
    )  
  }

  handleClick(e) {
    switch (e) {

      case 'uploadFile' : {
        console.log("准备上传")
        ImagePicker.showImagePicker(options, (response) => {
          console.log('Response = ', response);
          if (response.didCancel) {
            console.log('User cancelled image picker');
          }
          else if (response.error) {
            console.log('ImagePicker Error: ', response.error);
          }
          else if (response.customButton) {
            console.log('User tapped custom button: ', response.customButton);
          }
          else {
            
            let source = { uri: response.uri };
            
            AliyunOSS.asyncUpload("luozhang002", "xxxxxxxx/yanxing", source.uri).then((res)=>{
              Alert.alert(
                'Alert Title',
                "恭喜你成功上传到阿里云服务器",
                [
                  {text: 'Ask me later', onPress: () => console.log('Ask me later pressed')},
                  {text: 'Cancel', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
                  {text: 'OK', onPress: () => console.log('OK Pressed')},
                ],
                { cancelable: false }
              )
            })
          }
        });
      } break;

      case "appendObject" : {

         ImagePicker.showImagePicker(options, (response) => {
          console.log('Response = ', response);
          if (response.didCancel) {
            console.log('User cancelled image picker');
          }
          else if (response.error) {
            console.log('ImagePicker Error: ', response.error);
          }
          else if (response.customButton) {
            console.log('User tapped custom button: ', response.customButton);
          }
          else {
            
            let source = { uri: response.uri };
            let position = 0;
            AliyunOSS.asyncAppendObject("luozhang002", "xxx", source.uri,{appendPostion:`${position}`}).then((res)=>{
              nextPositon = res.nextPositon;
              //再次调用即可
            })
          }
        });
      } break;

      case "resumeObject" : {  
        ImagePicker.showImagePicker(options, (response) => {
          console.log('Response = ', response);
          if (response.didCancel) {
            console.log('User cancelled image picker');
          }
          else if (response.error) {
            console.log('ImagePicker Error: ', response.error);
          }
          else if (response.customButton) {
            console.log('User tapped custom button: ', response.customButton);
          }
          else {
            let source = { uri: response.uri };
            AliyunOSS.asyncResumableUpload("luozhang002", "zhongji", source.uri).then((res)=>{
              Alert.alert(
                'Alert Title',
                "恭喜你上传成功",
                [
                  {text: 'Ask me later', onPress: () => console.log('Ask me later pressed')},
                  {text: 'Cancel', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
                  {text: 'OK', onPress: () => console.log('OK Pressed')},
                ],
                { cancelable: false }
                )
            })
          }
        });
      } break;

      case "initMultipartUpload" : {
        AliyunOSS.initMultipartUpload(multipartBucket,multipartBucket).then((e)=>{
          Alert.alert("分片初始化成功：" + e);
          uploadId = e;
        }).catch((error)=>{
          console.log(error)
        })
      } break

      case "multipartUpload" : {
        
        ImagePicker.showImagePicker(options, (response) => {
          console.log('Response = ', response);

          if (response.didCancel) {
            console.log('User cancelled image picker');
          } else if (response.error) {
            console.log('ImagePicker Error: ', response.error);
          } else if (response.customButton) {
            console.log('User tapped custom button: ', response.customButton);
          } else {
            let source = { uri: response.uri };
            AliyunOSS.multipartUpload(multipartBucket,mulitipartObject,uploadId,source.uri).then((res)=>{
              Alert.alert("分片上传成功");
            }).catch((e)=>{
              Alert.alert("分片上传失败");
            })
          }
        });
      } break;

      case "abortMultipartUpload" : {
        AliyunOSS.abortMultipartUpload(multipartBucket,multipartBucket,uploadId).then((e)=>{
          Alert.alert("分片终止成功");
        }).catch((e)=>{
          Alert.alert("分片终止失败");
        })
      } break;

      case "listParts" : {

        AliyunOSS.listParts(multipartBucket,multipartBucket,uploadId).then((e)=>{
          Alert.alert("onListParts"+e)
        }).catch((e)=>{
          Alert.alert("onListPartsError")
        })

      } break;

      default : break;
    }
  }

  componentDidMount() {
    const uploadProgress = p => console.log(p.currentSize / p.totalSize);
    AliyunOSS.addEventListener('uploadProgress', uploadProgress);
  }
  
 }