package de.justjanne.quasseldroid.service

import android.util.Log
import de.justjanne.libquassel.client.session.ClientSession
import de.justjanne.libquassel.protocol.connection.ProtocolFeature
import de.justjanne.libquassel.protocol.connection.ProtocolMeta
import de.justjanne.libquassel.protocol.connection.ProtocolVersion
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.CoroutineChannel
import de.justjanne.libquassel.protocol.util.StateHolder
import de.justjanne.quasseldroid.messages.MessageStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.Closeable
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext

class QuasselRunner(
  private val address: InetSocketAddress,
  private val auth: Pair<String, String>
) : Thread("Quassel Runner"), Closeable, StateHolder<ClientSessionWrapper?> {
  private val channel = CoroutineChannel()

  override fun state(): ClientSessionWrapper? = state.value
  override fun flow(): StateFlow<ClientSessionWrapper?> = state

  private val state = MutableStateFlow<ClientSessionWrapper?>(null)

  init {
    start()
  }

  override fun run() {
    runBlocking(Dispatchers.IO) {
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
      state.value = ClientSessionWrapper(
        session,
        messages = MessageStore(
          session.rpcHandler.messages(),
          session.backlogManager
        )
      )
      session.handshakeHandler.init(
        "Quasseltest v0.1",
        "2022-02-24",
        FeatureSet.all()
      )
      val (username, password) = auth
      Log.d("QuasselRunner", "Authenticating")
      session.handshakeHandler.login(username, password)
      Log.d("QuasselRunner", "Waiting for init")
      session.baseInitHandler.waitForInitDone()
      Log.d("QuasselRunner", "Init Done")
    }
  }

  override fun close() {
    Log.d("QuasselRunner", "Stopping Quassel Runner")
    runBlocking(Dispatchers.IO) {
      withTimeout(2000L) {
        channel.close()
      }
    }
  }
}
