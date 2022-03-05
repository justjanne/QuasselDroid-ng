package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.justjanne.quasseldroid.service.ConnectionData
import de.justjanne.quasseldroid.util.extensions.handleTabFocus
import de.justjanne.quasseldroid.util.saver.TextFieldValueSaver
import java.net.InetSocketAddress

@Preview(name = "Login", showBackground = true)
@Composable
fun LoginView(
  default: ConnectionData? = null,
  onLogin: (ConnectionData) -> Unit = {}
) {
  val (host, setHost) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue(default?.address?.hostString ?: ""))
  }
  val (port, setPort) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue(default?.address?.port?.toString() ?: "4242"))
  }
  val (username, setUsername) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue(default?.username ?: ""))
  }
  val (password, setPassword) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue(default?.password ?: ""))
  }

  val focusManager = LocalFocusManager.current
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
  ) {
    OutlinedTextField(
      host,
      setHost,
      singleLine = true,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .handleTabFocus(focusManager),
      label = { Text("Hostname") },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = {
        focusManager.moveFocus(FocusDirection.Down)
      }),
    )
    OutlinedTextField(
      port,
      setPort,
      singleLine = true,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .handleTabFocus(focusManager),
      label = { Text("Port") },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = {
        focusManager.moveFocus(FocusDirection.Down)
      }),
    )
    OutlinedTextField(
      username,
      setUsername,
      singleLine = true,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .handleTabFocus(focusManager),
      label = { Text("Username") },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = {
        focusManager.moveFocus(FocusDirection.Down)
      }),
    )
    PasswordTextField(
      password,
      setPassword,
      singleLine = true,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .handleTabFocus(focusManager),
      label = { Text("Password") },
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
        autoCorrect = false,
        capitalization = KeyboardCapitalization.None,
        imeAction = ImeAction.Next
      ),
      keyboardActions = KeyboardActions(onNext = {
        focusManager.moveFocus(FocusDirection.Down)
      }),
    )
    Button(
      modifier = Modifier.padding(16.dp),
      onClick = {
        onLogin(
          ConnectionData(
            InetSocketAddress.createUnresolved(host.text, port.text.toIntOrNull() ?: 4242),
            username.text,
            password.text
          )
        )
      }
    ) {
      Text("Login")
    }
  }
}
