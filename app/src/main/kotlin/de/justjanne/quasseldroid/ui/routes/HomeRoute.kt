package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.justjanne.libquassel.protocol.models.BufferInfo
import de.justjanne.libquassel.protocol.models.ids.NetworkId
import de.justjanne.libquassel.protocol.models.network.NetworkInfo
import de.justjanne.libquassel.protocol.syncables.common.Network
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow
import de.justjanne.quasseldroid.util.saver.TextFieldValueSaver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Composable
fun HomeRoute(backend: QuasselBackend, navController: NavController) {
  val side = rememberFlow(null) {
    backend.flow().mapNullable { it.session.side }
  }

  val (buffer, setBuffer) = rememberSaveable(stateSaver = TextFieldValueSaver) {
    mutableStateOf(TextFieldValue(""))
  }

  val initStatus = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .mapNullable { it.baseInitHandler }
      .flatMap()
  }

  val buffers: List<Pair<NetworkInfo?, BufferInfo>> = rememberFlow(emptyList()) {
    val sessions = backend.flow()
      .mapNullable { it.session }
      .flatMap()

    val networks: Flow<Map<NetworkId, Network>> = sessions
      .mapNullable { it.networks }
      .map { it.orEmpty() }

    val buffers: Flow<List<BufferInfo>> = sessions
      .mapNullable { it.bufferSyncer }
      .flatMap()
      .mapNullable { it.bufferInfos.values.sortedBy(BufferInfo::bufferName) }
      .map { it.orEmpty() }

    combine(buffers, networks) { bufferList, networkMap ->
      bufferList.map {
        Pair(networkMap[it.networkId]?.networkInfo(), it)
      }
    }
  }

  val filteredBuffers = buffers.filter { (_, info) ->
    info.bufferName?.contains(buffer.text) == true
  }

  val context = LocalContext.current

  val scrollState = rememberLazyListState()

  Column {
    Text("Side: $side")
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
    TextField(value = buffer, onValueChange = setBuffer)
    LazyColumn(state = scrollState) {
      items(filteredBuffers, key = { (_, buffer) -> buffer.bufferId }) { (network, buffer) ->
        Column(modifier = Modifier
          .padding(4.dp)
          .fillMaxWidth()
          .clickable { navController.navigate("buffer/${buffer.bufferId.id}") }
        ) {
          Text(
            network?.networkName ?: "Unknown network",
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            buffer.type.joinToString(", "),
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            buffer.bufferName ?: "Unknown buffer",
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}


