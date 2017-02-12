import React from 'react'
import {
  AppRegistry,
  StyleSheet,
  Button,
  View,
} from 'react-native'
import Toast from './src/Nfc'

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
})

function showToast() {
  Toast.show('Hello world!', Toast.SHORT)
}

const MyApp = () => (
  <View style={styles.container}>
    <Button
      onPress={showToast}
      title="Learn More"
      color="#841584"
      accessibilityLabel="Learn more about this purple button"
    />
  </View>
)

export default MyApp

AppRegistry.registerComponent('MyApp', () => MyApp)
