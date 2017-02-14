import React from 'react'
import {
  AppRegistry,
  StyleSheet,
  Button,
  View,
  Text,
} from 'react-native'
import Nfc from './src/Nfc'

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


class MyApp extends React.Component {
  constructor(props) {
    super(props)
    this.state = {}
    this.showToast = this.showToast.bind(this)
  }

  showToast() {
    Nfc.getUID()
    .then(uid => this.setState({ uid }))
    .catch(error => this.setState({ error }))
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>{ this.state.uid || 'No UID' }</Text>
        <Text>{ this.state.error || 'No Error' }</Text>
        <Button
          onPress={this.showToast}
          title="Show me the message!"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
      </View>
    )
  }
}

export default MyApp

AppRegistry.registerComponent('MyApp', () => MyApp)
