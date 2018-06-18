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

export class BucketManager extends Component {

  render() {  
    return ( 
      <View style={styles.item}>
        <Text style={styles.description}>管理Bucket</Text>
        <View style={styles.detailitem}>
            <View style={styles.button}>
             <Button  style={styles.button}
                onPress={this.handClick.bind(this,"asyncCreateBucket")}
                title="创建bucket"
                color="#841584"/>
            </View>
          <View style={styles.button}>
            <Button style={styles.button}
              onPress={this.handClick.bind(this,"getBucketACL")}
              title="获取Bucket ACL "
              color="#841584"
            />
          </View>
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"getAllBuckets")}
              title="获取所有bucket"
              color="#841584"
            />
          </View>
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"deleteBucket")}
              title="删除bucket"
              color="#841584"
            />
          </View>
        </View>
      </View>
    )  
  }

  handClick(e) {
    switch(e){
      
      case 'asyncCreateBucket': {
        AliyunOSS.asyncCreateBucket('tyluoluo','private','oss-cn-zhangjiakou').then((e)=>{
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "getBucketACL": {
        
        AliyunOSS.asyncGetBucketACL('luozhang002').then((e)=>{
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        }) 
      } break;

      case "getAllBuckets": {
        
        AliyunOSS.asyncListBuckets().then((e) => {
            console.log(e)
          }).catch((e)=>{
            console.log(e)
        })
      } break;

      case 'deleteBucket': {
        AliyunOSS.asyncDeleteBucket("tyluoluo").then((e)=>{
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      default:break;
    } 
  } 
 }
