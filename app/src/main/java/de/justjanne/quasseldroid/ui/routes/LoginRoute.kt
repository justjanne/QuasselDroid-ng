package de.justjanne.quasseldroid.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.LoginView

@Composable
fun LoginRoute(backend: QuasselBackend, navController: NavController) {
  val context = LocalContext.current
    LoginView(onLogin = {
        if (backend.login(context, it)) {
            navController.navigate("home")
        }
    })
}
