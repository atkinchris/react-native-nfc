import React from 'react'
import {
  AppRegistry,
  StyleSheet,
  View,
  Text,
  DeviceEventEmitter,
  Button,
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

    this.getData = this.getData.bind(this)
    this.setUid = this.setUid.bind(this)
  }

  componentWillMount() {
    DeviceEventEmitter.addListener(Nfc.ON_TAG_PRESENT, this.setUid)
    DeviceEventEmitter.addListener(Nfc.ON_FAILURE, error => this.setState({ error }))
  }

  setUid(message) {
    this.setState({ message, error: null, data: null })
  }

  getData() {
    Nfc.getPage(5)
      .then(data => this.setState({ data }))
      .catch(error => this.setState({ error }))
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>{ JSON.stringify(this.state.message) || 'No Message' }</Text>
        <Text>{ JSON.stringify(this.state.error) || 'No Error' }</Text>
        <Text>{ JSON.stringify(this.state.data) || 'No Data' }</Text>
        <Button onPress={this.getData} title="Get Data" />
      </View>
    )
  }
}

export default MyApp

AppRegistry.registerComponent('MyApp', () => MyApp)
