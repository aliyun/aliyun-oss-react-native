import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Alert,
  Button,
  ScrollView,
  Image
} from 'react-native';

//导入样式
import { styles } from '../CSS/global.js' 

export class ObjectManager extends Component {  
  render() {  
     return(
      <View style={styles.item}>
        <Text style={styles.description}>管理文件</Text>
        <View style={styles.detailitem}>
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.clickHandle.bind(this,"asyncHeadObject")}
              title="asyncHeadObject"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.clickHandle.bind(this,"asyncListObjects")}
              title="asyncListObjects"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.clickHandle.bind(this,"asyncCopyObject")}
              title="asyncCopyObject"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.clickHandle.bind(this,"doesObjectExist")}
              title="doesObjectExist"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.clickHandle.bind(this,"asyncDeleteObject")}
              title="asyncDeleteObject"
              color="#841584"
            />
          </View>
        </View>
      </View>
    )  
  } 
  clickHandle(e) {
    switch(e) {
      case "asyncHeadObject" : {
        AliyunOSS.asyncHeadObject('luozhang002','yanxing').then((e) => {
          console.log(e)
        }).catch((e)=> {
          console.log(e)
        })
      } break;

      case "asyncListObjects" : {
        console.log("asyncListObjects")
        AliyunOSS.asyncListObjects('luozhang002',{
          prefix:'xxxx'
        }).then((e) => {
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "asyncCopyObject" : {
        AliyunOSS.asyncCopyObject('luozhang001',"2.png","luozhang002","sbsbsb").then((e) => {
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "doesObjectExist": {
        AliyunOSS.doesObjectExist('luozhang001','xx.png').then((e) => {
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "asyncDeleteObject" : {
        AliyunOSS.asyncDeleteObject('luozhang001','2.png').then((e)=>{
          Alert.alert(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      default :break;
    }
  }
 }