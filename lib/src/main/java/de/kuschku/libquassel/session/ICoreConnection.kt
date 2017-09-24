package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import org.reactivestreams.Publisher

interface ICoreConnection {
  val state: Publisher<ConnectionState>
  fun close()
  fun dispatch(message: HandshakeMessage)
  fun dispatch(message: SignalProxyMessage)
  fun start()
  fun join()
  fun setState(value: ConnectionState)

  companion object {
    val NULL = object : ICoreConnection {
      override fun setState(value: ConnectionState) = Unit
      override fun start() = Unit
      override fun join() = Unit
      override fun close() = Unit
      override fun dispatch(message: HandshakeMessage) = Unit
      override fun dispatch(message: SignalProxyMessage) = Unit
      override val state: Publisher<ConnectionState>
        get() = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)
          .toFlowable(BackpressureStrategy.LATEST)
    }
  }
}
