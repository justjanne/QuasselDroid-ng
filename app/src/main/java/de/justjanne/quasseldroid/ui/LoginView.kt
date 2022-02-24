package de.justjanne.quasseldroid.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.justjanne.quasseldroid.service.ConnectionData
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.util.TextFieldValueSaver
import java.net.InetSocketAddress

@Composable
fun LoginRoute(backend: QuasselBackend, navController: NavController) {
  val context = LocalContext.current
  LoginView(onLogin = {
    if (backend.login(context, it)) {
      navController.navigate("home")
    }
  })
}

@Preview(name = "Login", showBackground = true)
@Composable
fun LoginView(onLogin: (ConnectionData) -> Unit = {}) {
  val (host, setHost) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue())
  }
  val (port, setPort) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue("4242"))
  }
  val (username, setUsername) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue())
  }
  val (password, setPassword) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue())
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
        .fillMaxWidth(),
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
        .fillMaxWidth(),
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
        .fillMaxWidth(),
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
        .fillMaxWidth(),
      label = { Text("Password") },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
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
