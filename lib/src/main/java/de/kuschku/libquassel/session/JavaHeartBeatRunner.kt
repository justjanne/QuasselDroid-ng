package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class JavaHeartBeatRunner(
  private val session: Session
) : Thread(), HeartBeatRunner {
  private var running = true
  private var lastHeartBeatReply: Instant = Instant.now()

  override fun start() {
    while (running) {
      val now = Instant.now()
      val duration = Duration.between(lastHeartBeatReply, now).toMillis()
      if (duration > TIMEOUT) {
        log(INFO, "Heartbeat", "Ping Timeout: Last Response ${duration}ms ago")
        session.close()
      } else {
        log(INFO, "Heartbeat", "Sending Heartbeat")
        session.dispatch(SignalProxyMessage.HeartBeat(now))
      }
      Thread.sleep(DELAY)
    }
  }

  override fun end() {
    running = false
  }

  override fun setLastHeartBeatReply(time: Instant) {
    this.lastHeartBeatReply = time
  }

  companion object {
    const val TIMEOUT = 120_000L
    const val DELAY = 30_000L
  }
}
