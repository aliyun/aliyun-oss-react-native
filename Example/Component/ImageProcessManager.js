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

export class ImageProcessManager extends Component {

	render() {
		return (
      <View style={styles.item}>
        <Text style={styles.description}>图片处理</Text>      
        <View style={styles.detailitem}>  
          
          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"GeShi")}
              title="格式转换"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"Scale")}
              title="缩放比例"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"Cut")}
              title="裁剪比例"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"Rotate")}
              title="旋转操作"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"Effect")}
              title="图片效果"
              color="#841584"
            />
          </View>

          <View style={styles.button}>
            <Button  style={styles.button}
              onPress={this.handClick.bind(this,"ShuiYin")}
              title="水印操作"
              color="#841584"
            />
          </View>

        </View>
      </View>
		)
	}

  handClick (e) {
    switch (e) {
      
      case "GeShi" : {
        //备注第三个参数必须穿入字符串
        AliyunOSS.asyncDownload('luozhang002','yanxing',"",{"x-oss-process":'image/format,jpg'}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;


      case 'Scale': {
        AliyunOSS.asyncDownload('luozhang002','yanxing',"",{"x-oss-process":'image/resize,h_100'}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e) => {
          console.log(e)
        })
      } break;

      case 'cut' : {
        AliyunOSS.asyncDownload('luozhang002','yanxing',"",{"x-oss-process":'image/circle,r_100'}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case 'Rotate': {
        AliyunOSS.asyncDownload('luozhang002','yanxing',"",{"x-oss-process":'image/resize,w_100/auto-orient,1'}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "Effect": {
         // image/bright,50
        AliyunOSS.asyncDownload('luozhang002','yanxing',"",{"x-oss-process":'image/bright,50'}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      case "ShuiYin" : {
        let xOss = "image/resize,w_400/watermark,image_cGFuZGEucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfMzA,t_90,g_se,x_10,y_10"
        AliyunOSS.asyncDownload('luozhang002','zhongji',"",{"x-oss-process":xOss}).then((e)=>{
          Alert.alert(e)
          console.log(e)
        }).catch((e)=>{
          console.log(e)
        })
      } break;

      default : break;
    } //end switch 

  }
}