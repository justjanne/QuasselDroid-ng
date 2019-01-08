package de.kuschku.quasseldroid.service

import android.os.Handler
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.session.HeartBeatRunner
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class AndroidHeartBeatRunner : HeartBeatRunner {
  private val handler = Handler()
  private var running = true
  private var lastHeartBeatReply: Instant = Instant.now()
  private var lastHeartBeatSend: Instant = Instant.now()

  private var closeCallback: (() -> Unit)? = null
  private var heartbeatDispatchCallback: ((SignalProxyMessage.HeartBeat) -> Unit)? = null

  override fun setCloseCallback(callback: (() -> Unit)?) {
    this.closeCallback = callback
  }

  override fun setHeartbeatDispatchCallback(callback: ((SignalProxyMessage.HeartBeat) -> Unit)?) {
    this.heartbeatDispatchCallback = callback
  }

  override fun start() {
    if (running) {
      val duration = Duration.between(lastHeartBeatReply, lastHeartBeatSend).toMillis()
      if (duration > TIMEOUT) {
        log(INFO, "Heartbeat", "Ping Timeout: Last Response ${duration}ms ago")
        closeCallback?.invoke()
      } else {
        log(INFO, "Heartbeat", "Sending Heartbeat")
        val now = Instant.now()
        lastHeartBeatSend = now
        heartbeatDispatchCallback?.invoke(SignalProxyMessage.HeartBeat(now))
      }
      handler.postDelayed(::start, DELAY)
    }
  }

  override fun end() {
    running = false
    setCloseCallback(null)
    setHeartbeatDispatchCallback(null)
  }

  override fun setLastHeartBeatReply(time: Instant) {
    this.lastHeartBeatReply = time
  }

  companion object {
    const val TIMEOUT = 90_000L
    const val DELAY = 30_000L
  }
}
