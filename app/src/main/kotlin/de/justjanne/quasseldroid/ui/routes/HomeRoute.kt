package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.MessageView
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow
import de.justjanne.quasseldroid.util.saver.BufferIdSaver
import kotlinx.coroutines.flow.map

@Composable
fun HomeRoute(backend: QuasselBackend, navController: NavController) {
  val session = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
  }

  val (buffer, setBuffer) = rememberSaveable(stateSaver = BufferIdSaver) {
    mutableStateOf(BufferId(-1))
  }

  val messages: List<Message> = rememberFlow(emptyList()) {
    backend.flow()
      .mapNullable { it.messages }
      .flatMap()
      .mapNullable { it[buffer] }
      .map { it?.messages.orEmpty() }
  }

  val initStatus = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
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
    LazyColumn {
      items(messages, key = Message::messageId) {
        MessageView(it)
      }
    }
  }
}


