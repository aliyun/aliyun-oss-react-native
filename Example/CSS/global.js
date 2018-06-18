
import { StyleSheet } from 'react-native'

export const styles = StyleSheet.create({
  container: {
    flexDirection:'column',
    backgroundColor: '#F5FCFF',
    flexWrap:'wrap'
  },
  description: {
    fontSize:20,
    marginTop:10,
    marginLeft:10,
    marginBottom:20
  },
  item: {
    justifyContent:'space-around',
    marginBottom:10
  },
  button:{
    margin:10
  },
  detailitem: {
    flexDirection:'row',
    justifyContent:'flex-start',
    flexWrap:'wrap'
  }
});