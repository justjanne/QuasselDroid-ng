/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.malheur.collectors

import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.CrashHandler
import de.kuschku.malheur.config.ThreadConfig
import de.kuschku.malheur.data.ThreadInfo
import de.kuschku.malheur.data.ThreadsInfo
import de.kuschku.malheur.data.TraceElement

class ThreadCollector : Collector<ThreadsInfo, ThreadConfig> {
  override fun collect(context: CrashContext, config: ThreadConfig) = ThreadsInfo(
    crashed = context.stackTraces?.filterKeys {
      it == context.crashingThread
    }?.map { (thread, stackTrace) ->
      threadToInfo(thread, stackTrace)
    }?.firstOrNull(),
    others = context.stackTraces?.filterKeys {
      it != Thread.currentThread() && it != context.crashingThread
    }?.map { (thread, stackTrace) ->
      threadToInfo(thread, stackTrace)
    }
  )

  private fun threadToInfo(thread: Thread, stackTrace: Array<StackTraceElement>) = ThreadInfo(
    id = thread.id,
    name = thread.name,
    group = thread.threadGroup?.name,
    status = thread.state?.name,
    stackTrace = ArrayList(sanitize(stackTrace.map(::TraceElement))),
    isDaemon = thread.isDaemon,
    priority = thread.priority
  )

  private fun sanitize(list: List<TraceElement>): List<TraceElement> {
    var idx = 0
    while (idx < list.size) {
      val traceElement = list[idx]
      if (traceElement.className == CrashHandler::class.java.canonicalName)
        break
      idx++
    }
    while (idx < list.size) {
      val traceElement = list[idx]
      if (traceElement.className != CrashHandler::class.java.canonicalName)
        break
      idx++
    }
    val after = mutableListOf<TraceElement>()
    while (idx < list.size) {
      val traceElement = list[idx]
      after.add(traceElement)
      idx++
    }

    return if (after.size > 0) {
      after
    } else list
  }
}
