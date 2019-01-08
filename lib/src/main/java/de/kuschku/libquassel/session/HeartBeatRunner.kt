package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import org.threeten.bp.Instant

interface HeartBeatRunner {
  fun setCloseCallback(callback: (() -> Unit)?)
  fun setHeartbeatDispatchCallback(callback: ((SignalProxyMessage.HeartBeat) -> Unit)?)

  fun start()
  fun end()

  fun setLastHeartBeatReply(time: Instant)

  companion object {
    const val TIMEOUT = 120_000L
    const val DELAY = 30_000L
  }
}
