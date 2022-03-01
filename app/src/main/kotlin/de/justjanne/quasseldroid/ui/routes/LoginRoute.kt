package de.justjanne.quasseldroid.ui.routes

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.navigation.NavController
import de.justjanne.quasseldroid.service.ConnectionData
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.LoginView
import java.net.InetSocketAddress

@Composable
fun LoginRoute(backend: QuasselBackend, navController: NavController) {
  val context = LocalContext.current
  LoginView(default = loadConnectionData(context)) {
    if (backend.login(context, it)) {
      navController.navigate("home")
      saveConnectionData(context, it)
    }
  }
}

fun loadConnectionData(context: Context): ConnectionData? {
  val sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
  return ConnectionData(
    address = InetSocketAddress.createUnresolved(
      sharedPreferences.getString("host", null)
        ?: return null,
      sharedPreferences.getInt("port", 0)
        .takeIf { it > 0 }
        ?: return null,
    ),
    username = sharedPreferences.getString("username", null)
      ?: return null,
    password = sharedPreferences.getString("password", null)
      ?: return null
  )
}

fun saveConnectionData(context: Context, connectionData: ConnectionData) {
  val sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
  sharedPreferences.edit {
    putString("host", connectionData.address.hostString)
    putInt("port", connectionData.address.port)
    putString("username", connectionData.username)
    putString("password", connectionData.password)
  }
}
