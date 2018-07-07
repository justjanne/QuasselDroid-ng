package de.kuschku.quasseldroid.service

import android.os.Handler
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.session.HeartBeatRunner
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class AndroidHeartBeatRunner(
  private val session: Session,
  private val handler: Handler
) : HeartBeatRunner {
  private var running = true
  private var lastHeartBeatReply: Instant = Instant.now()
  private var lastHeartBeatSend: Instant = Instant.now()

  override fun start() {
    if (running) {
      val duration = Duration.between(lastHeartBeatReply, lastHeartBeatSend).toMillis()
      if (duration > TIMEOUT) {
        log(INFO, "Heartbeat", "Ping Timeout: Last Response ${duration}ms ago")
        session.close()
      } else {
        log(INFO, "Heartbeat", "Sending Heartbeat")
        val now = Instant.now()
        lastHeartBeatSend = now
        session.dispatch(SignalProxyMessage.HeartBeat(now))
      }
      handler.postDelayed(::start, DELAY)
    }
  }

  override fun end() {
    running = false
  }

  override fun setLastHeartBeatReply(time: Instant) {
    this.lastHeartBeatReply = time
  }

  companion object {
    const val TIMEOUT = 90_000L
    const val DELAY = 30_000L
  }
}
