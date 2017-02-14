import React from 'react'
import {
  AppRegistry,
  StyleSheet,
  View,
  Text,
  DeviceEventEmitter,
} from 'react-native'
import Nfc from './src/Nfc'

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
})

class MyApp extends React.Component {
  constructor(props) {
    super(props)
    this.state = {}
  }

  componentWillMount() {
    DeviceEventEmitter.addListener(Nfc.ON_SUCCESS, message => this.setState({ message }))
    DeviceEventEmitter.addListener(Nfc.ON_FAILURE, error => this.setState({ error }))
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>{ JSON.stringify(this.state.message) || 'No Message' }</Text>
        <Text>{ JSON.stringify(this.state.error) || 'No Error' }</Text>
      </View>
    )
  }
}

export default MyApp

AppRegistry.registerComponent('MyApp', () => MyApp)
