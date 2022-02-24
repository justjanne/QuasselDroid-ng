package de.justjanne.quasseldroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.CoreInfoRoute
import de.justjanne.quasseldroid.ui.HomeView
import de.justjanne.quasseldroid.ui.LoginRoute

@Composable
fun QuasseldroidRouter(backend: QuasselBackend) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = "login") {
    composable("login") { LoginRoute(backend, navController) }
    composable("home") { HomeView(backend, navController) }
    composable("coreInfo") { CoreInfoRoute(backend, navController) }
  }
}
