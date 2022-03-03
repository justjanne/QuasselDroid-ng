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
fun MessageRoute(
  backend: QuasselBackend,
  navController: NavController,
  buffer: BufferId
) {
  val listState = rememberLazyListState()

  val messageStore: MessageStore? = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.messages }
  }

  val messages: List<Message> = rememberFlow(emptyList()) {
    backend.flow()
      .mapNullable { it.messages }
      .flatMap()
      .mapNullable { it[buffer] }
      .map { it?.messages.orEmpty() }
  }

  val markerLine: MsgId? = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .flatMap()
      .mapNullable { it.bufferSyncer }
      .flatMap()
      .mapNullable { it.markerLines[buffer] }
  }

  Column {
    Row {
      Button(onClick = { navController.navigate("home") }) {
        Text("Back")
      }
      Button(onClick = {
        messageStore?.loadBefore(buffer, limit)
      }) {
        Text("↑")
      }
      Button(onClick = {
        messageStore?.loadAfter(buffer, limit)
      }) {
        Text("↓")
      }
      Button(onClick = {
        messageStore?.loadAround(buffer, markerLine ?: MsgId(-1), limit)
      }) {
        Text("N")
      }
      Button(onClick = {
        messageStore?.clear(buffer)
      }) {
        Text("Clr")
      }
    }
    MessageList(
      messages = messages,
      listState = listState,
      markerLine = markerLine ?: MsgId(-1),
      buffer = 5,
      onLoadAtStart = { messageStore?.loadBefore(buffer, limit) },
      onLoadAtEnd = { messageStore?.loadAfter(buffer, limit) }
    )
  }
}


