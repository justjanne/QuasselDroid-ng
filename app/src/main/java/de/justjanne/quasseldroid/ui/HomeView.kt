package de.justjanne.quasseldroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.syncables.state.BufferViewConfigState
import de.justjanne.libquassel.protocol.util.combineLatest
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow

@Composable
fun HomeView(backend: QuasselBackend, navController: NavController) {
  val session = rememberFlow(null) { backend.flow() }

  val bufferViewConfigs: List<BufferViewConfigState> = rememberFlow(emptyList()) {
    backend.flow()
      .flatMap()
      .mapNullable { it.bufferViewManager }
      .flatMap()
      .mapNullable { it.bufferViewConfigs() }
      .combineLatest()
  }

  val initStatus = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.baseInitHandler }
      .flatMap()
  }

  val context = LocalContext.current
  Column {
    Text("Side: ${session?.side}")
    if (initStatus != null) {
      val done = initStatus.total - initStatus.waiting.size
      Text("Init: ${initStatus.started} $done/ ${initStatus.total}")
    }
    Button(onClick = { navController.navigate("coreInfo") }) {
      Text("Core Info")
    }
    Button(onClick = {
      backend.disconnect(context)
      navController.navigate("login")
    }) {
      Text("Disconnect")
    }
    Text("BufferViewConfigs: ${bufferViewConfigs.size}")
    LazyColumn {
      items(bufferViewConfigs, key = BufferViewConfigState::bufferViewId) {
        Row {
          Text("${it.bufferViewId}: ${it.bufferViewName}")
        }
      }
    }
  }
}
