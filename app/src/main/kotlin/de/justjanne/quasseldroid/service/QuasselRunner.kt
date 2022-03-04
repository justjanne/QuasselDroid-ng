package de.justjanne.quasseldroid.service

import android.util.Log
import de.justjanne.libquassel.client.session.ClientSession
import de.justjanne.libquassel.protocol.connection.ProtocolFeature
import de.justjanne.libquassel.protocol.connection.ProtocolMeta
import de.justjanne.libquassel.protocol.connection.ProtocolVersion
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.CoroutineChannel
import de.justjanne.libquassel.protocol.util.StateHolder
import de.justjanne.quasseldroid.persistence.AppDatabase
import de.justjanne.quasseldroid.persistence.MessageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeout
import java.io.Closeable
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext

class QuasselRunner(
  private val address: InetSocketAddress,
  private val auth: Pair<String, String>,
  private val database: AppDatabase
) : Closeable, StateHolder<ClientSession?>, CoroutineScope {
  private val channel = CoroutineChannel()

  override fun state(): ClientSession? = state.value
  override fun flow(): StateFlow<ClientSession?> = state

  private val state = MutableStateFlow<ClientSession?>(null)

  override val coroutineContext = newSingleThreadContext("Quassel Runner")

  private val job = launch {
    Log.d("QuasselRunner", "Resolving URL")
    val address = InetSocketAddress(address.hostString, address.port)
    Log.d("QuasselRunner", "Connecting")
    channel.connect(address)
    Log.d("QuasselRunner", "Handshake")
    val session = ClientSession(
      channel,
      ProtocolFeature.all,
      listOf(
        ProtocolMeta(
          ProtocolVersion.Datastream,
          0x0000u
        )
      ),
      SSLContext.getDefault()
    )
    state.value = session

    with(session) {
      handshakeHandler.init(
        "Quasseltest v0.1",
        "2022-02-24",
        FeatureSet.all()
      )
      val (username, password) = auth
      Log.d("QuasselRunner", "Authenticating")
      handshakeHandler.login(username, password)
      Log.d("QuasselRunner", "Waiting for init")
      baseInitHandler.waitForInitDone()
      Log.d("QuasselRunner", "Init Done")
      rpcHandler.messages().collectIndexed { _, message ->
        database.messageDao().insert(MessageModel(message))
      }
    }
  }

  override fun close() {
    Log.d("QuasselRunner", "Stopping Quassel Runner")
    runBlocking(Dispatchers.IO) {
      withTimeout(2000L) {
        job.cancelAndJoin()
        runInterruptible {
          coroutineContext.cancel()
          channel.close()
        }
      }
    }
  }
}
