package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.CoreInfoView
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow

@Composable
fun CoreInfoRoute(backend: QuasselBackend, navController: NavController) {
  val coreInfo = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .flatMap()
      .mapNullable { it.coreInfo }
      .flatMap()
  }

  Column(Modifier.padding(16.dp)) {
    Button(onClick = { navController.navigate("home") }) {
      Text("Back")
    }

    if (coreInfo == null) {
      Text("No data available")
    } else {
      CoreInfoView(coreInfo)
    }
  }
}
