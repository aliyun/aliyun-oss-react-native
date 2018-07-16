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


const configuration = {
  maxRetryCount: 3,  
  timeoutIntervalForRequest: 30,
  timeoutIntervalForResource: 24 * 60 * 60
};

const config = {
  AccessKey: 'XXX',
  SecretKey: 'XXX',
};

const STSConfig = {
  AccessKeyId:'XXX',
  SecretKeyId:'XXX',
  SecurityToken:'XXX'
}

const endPoint = 'oss-cn-zhangjiakou.aliyuncs.com';
const companyserver = "http://XXX:PORT"
const familyserver = "http://XXX:PORT"

//导入样式
import { styles } from '../CSS/global.js' 

export class AuthManager extends Component {
  
  render() {
    return (
      <View style={styles.item}>
        <Text style={styles.description}>Client初始化</Text>
        <View style={styles.detailitem}>
          
          <View style={styles.button}>
            <Button
              onPress={this.handClick.bind(this,"AKSK")}
              title="AKSK明文"
              color="#841584"
            />
          </View> 
        
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"Singer")}
              title="自签"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"STS")}
              title="STS"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"ServerSTS")}
              title="Server STS"
              color="#841584"
            />
          </View>
        </View>
      </View>
    )
  }

  handClick(e) {
    switch(e) {

      case 'AKSK' : {
        Alert.alert(Platform.OS);
        if (Platform.OS == 'ios') {
          AliyunOSS.initWithPlainTextAccessKey(config.AccessKey,config.SecretKey,endPoint,configuration);
          Alert.alert("initAKSK success!")
        } else {
          Alert.alert('android do not support AK SK ,please use initWithSTS')
        }
      } break;

      case 'Singer' : {
        Alert.alert('initAKSK')
      } break;

      case "STS": {
        AliyunOSS.initWithSecurityToken(STSConfig.SecurityToken,STSConfig.AccessKeyId,STSConfig.SecretKeyId,endPoint,configuration)
        Alert.alert('STS success!')
      } break;

      case "ServerSTS" : {
        AliyunOSS.initWithServerSTS(familyserver,endPoint, configuration)
        Alert.alert('initServerSTS Success!')
      } break;

      default: break;
     }
  }
}