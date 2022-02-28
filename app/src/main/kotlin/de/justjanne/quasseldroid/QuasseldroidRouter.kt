package de.justjanne.quasseldroid

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.routes.CoreInfoRoute
import de.justjanne.quasseldroid.ui.routes.HomeRoute
import de.justjanne.quasseldroid.ui.routes.LoginRoute

@Composable
fun QuasseldroidRouter(backend: QuasselBackend) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = "login") {
    composable("login") {
      LoginRoute(backend, navController)
    }
    composable("home") {
      HomeRoute(backend, navController)
    }

    composable(
      "buffer/{bufferId}",
      listOf(navArgument("bufferId") { type = NavType.IntType })
    ) {
      Text("Buffer ${it.arguments?.getInt("bufferId")}")
    }
    composable("bufferViewConfigs") {
      Text("List of BufferViewConfigs")
    }
    composable(
      "bufferViewConfigs/{bufferViewConfigId}",
      listOf(navArgument("bufferViewConfigId") { type = NavType.IntType })
    ) {
      Text("BufferViewConfig ${it.arguments?.getInt("bufferViewConfigId")}")
    }

    composable("coreInfo") {
      CoreInfoRoute(backend, navController)
    }
  }
}
