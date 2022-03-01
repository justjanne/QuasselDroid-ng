package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.messages.MessageStore
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.MessageList
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow
import de.justjanne.quasseldroid.util.saver.TextFieldValueSaver
import kotlinx.coroutines.flow.map

private const val limit = 20

@Composable
fun HomeRoute(backend: QuasselBackend, navController: NavController) {
  val session = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
  }

  val (buffer, setBuffer) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue("3747"))
  }
  val (position, setPosition) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue("108113920"))
  }

  val bufferId = BufferId(buffer.text.toIntOrNull() ?: -1)
  val positionId = MsgId(position.text.toLongOrNull() ?: -1L)

  val listState = rememberLazyListState()

  val messageStore: MessageStore? = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.messages }
  }

  val messages: List<Message> = rememberFlow(emptyList()) {
    backend.flow()
      .mapNullable { it.messages }
      .flatMap()
      .mapNullable { it[bufferId] }
      .map { it?.messages.orEmpty() }
  }

  val markerLine: MsgId? = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .flatMap()
      .mapNullable { it.bufferSyncer }
      .flatMap()
      .mapNullable { it.markerLines[bufferId] }
  }

  val initStatus = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .mapNullable { it.baseInitHandler }
      .flatMap()
  }

  val context = LocalContext.current
  val buttonScrollState = rememberScrollState()

  Column {
    Text("Side: ${session?.side}")
    if (initStatus != null) {
      val done = initStatus.total - initStatus.waiting.size
      Text("Init: ${initStatus.started} $done/ ${initStatus.total}")
    }
    Row(modifier = Modifier.horizontalScroll(buttonScrollState)) {
      Button(onClick = { navController.navigate("coreInfo") }) {
        Text("Core Info")
      }
      Button(onClick = {
        backend.disconnect(context)
        navController.navigate("login")
      }) {
        Text("Disconnect")
      }
      Button(onClick = {
        messageStore?.loadBefore(bufferId, limit)
      }) {
        Text("↑")
      }
      Button(onClick = {
        messageStore?.loadAfter(bufferId, limit)
      }) {
        Text("↓")
      }
      Button(onClick = {
        messageStore?.loadAround(bufferId, positionId, limit)
      }) {
        Text("…")
      }
    }
    TextField(value = buffer, onValueChange = setBuffer)
    TextField(value = position, onValueChange = setPosition)
    MessageList(
      messages = messages,
      listState = listState,
      markerLine = markerLine ?: MsgId(-1),
      buffer = 5,
      onLoadAtStart = { messageStore?.loadBefore(bufferId, limit) },
      onLoadAtEnd = { messageStore?.loadAfter(bufferId, limit) }
    )
  }
}


