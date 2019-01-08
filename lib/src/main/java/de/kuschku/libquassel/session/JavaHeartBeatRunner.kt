/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class JavaHeartBeatRunner : Thread(), HeartBeatRunner {
  private var running = true
  private var lastHeartBeatReply: Instant = Instant.now()

  private var closeCallback: (() -> Unit)? = null
  private var heartbeatDispatchCallback: ((SignalProxyMessage.HeartBeat) -> Unit)? = null

  override fun setCloseCallback(callback: (() -> Unit)?) {
    this.closeCallback = callback
  }

  override fun setHeartbeatDispatchCallback(callback: ((SignalProxyMessage.HeartBeat) -> Unit)?) {
    this.heartbeatDispatchCallback = callback
  }

  override fun start() {
    while (running) {
      val now = Instant.now()
      val duration = Duration.between(lastHeartBeatReply, now).toMillis()
      if (duration > TIMEOUT) {
        log(INFO, "Heartbeat", "Ping Timeout: Last Response ${duration}ms ago")
        closeCallback?.invoke()
      } else {
        log(INFO, "Heartbeat", "Sending Heartbeat")
        heartbeatDispatchCallback?.invoke(SignalProxyMessage.HeartBeat(now))
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
