import { AppRegistry } from 'react-native';
import App from './App';

//iOS 可运行模块
AppRegistry.registerComponent('NativeAddRN', () => App);

// android可运行模块
AppRegistry.registerComponent('Example', () => App);
