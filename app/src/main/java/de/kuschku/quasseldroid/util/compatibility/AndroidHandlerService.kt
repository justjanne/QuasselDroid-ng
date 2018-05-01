/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.compatibility

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import de.kuschku.libquassel.util.compatibility.HandlerService
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

class AndroidHandlerService : HandlerService {
  override lateinit var scheduler: Scheduler

  override fun serialize(f: () -> Unit) {
    serializeHandler.post(f)
  }

  override fun deserialize(f: () -> Unit) {
    deserializeHandler.post(f)
  }

  override fun write(f: () -> Unit) {
    writeHandler.post(f)
  }

  override fun backend(f: () -> Unit) {
    backendHandler.post(f)
  }

  override fun backendDelayed(delayMillis: Long, f: () -> Unit) {
    backendHandler.postDelayed(f, delayMillis)
  }

  private val serializeThread = HandlerThread("serialize", Process.THREAD_PRIORITY_BACKGROUND)
  private val deserializeThread = HandlerThread("deserialize", Process.THREAD_PRIORITY_BACKGROUND)
  private val writeThread = HandlerThread("write", Process.THREAD_PRIORITY_BACKGROUND)
  private val backendThread = HandlerThread("backend", Process.THREAD_PRIORITY_BACKGROUND)

  private val serializeHandler: Handler
  private val deserializeHandler: Handler
  private val writeHandler: Handler
  private val backendHandler: Handler

  private val internalExceptionHandler = Thread.UncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
    val exceptionHandler = exceptionHandler ?: Thread.getDefaultUncaughtExceptionHandler()
    exceptionHandler.uncaughtException(thread, throwable)
  }

  init {
    serializeThread.uncaughtExceptionHandler = internalExceptionHandler
    deserializeThread.uncaughtExceptionHandler = internalExceptionHandler
    writeThread.uncaughtExceptionHandler = internalExceptionHandler
    backendThread.uncaughtExceptionHandler = internalExceptionHandler

    serializeThread.start()
    deserializeThread.start()
    writeThread.start()
    backendThread.start()

    serializeHandler = Handler(serializeThread.looper)
    deserializeHandler = Handler(deserializeThread.looper)
    writeHandler = Handler(writeThread.looper)
    backendHandler = Handler(backendThread.looper)

    scheduler = HandlerScheduler(backendHandler)
  }

  override var exceptionHandler: Thread.UncaughtExceptionHandler? = null
    get() = field ?: Thread.getDefaultUncaughtExceptionHandler()

  override fun quit() {
    serializeThread.quit()
    deserializeThread.quit()
    writeThread.quit()
    backendThread.quit()
  }

  internal class HandlerScheduler(private val handler: Handler) : Scheduler() {
    override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
      val scheduled = ScheduledRunnable(handler, RxJavaPlugins.onSchedule(run))
      handler.postDelayed(scheduled, unit.toMillis(delay))
      return scheduled
    }

    override fun createWorker(): Scheduler.Worker {
      return HandlerWorker(handler)
    }

    private class HandlerWorker constructor(private val handler: Handler) : Scheduler.Worker() {
      @Volatile
      private var disposed: Boolean = false

      override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        if (disposed) {
          return Disposables.disposed()
        }

        val scheduled = ScheduledRunnable(handler, RxJavaPlugins.onSchedule(run))

        val message = Message.obtain(handler, scheduled)
        message.obj = this // Used as token for batch disposal of this worker's runnables.

        handler.sendMessageDelayed(message, unit.toMillis(delay))

        // Re-check disposed state for removing in case we were racing a call to dispose().
        if (disposed) {
          handler.removeCallbacks(scheduled)
          return Disposables.disposed()
        }

        return scheduled
      }

      override fun dispose() {
        disposed = true
        handler.removeCallbacksAndMessages(this /* token */)
      }

      override fun isDisposed(): Boolean {
        return disposed
      }
    }

    private class ScheduledRunnable internal constructor(
      private val handler: Handler,
      private val delegate: Runnable
    ) : Runnable, Disposable {
      @Volatile
      private var disposed: Boolean = false

      override fun run() {
        try {
          delegate.run()
        } catch (t: Throwable) {
          RxJavaPlugins.onError(t)
        }

      }

      override fun dispose() {
        disposed = true
        handler.removeCallbacks(this)
      }

      override fun isDisposed(): Boolean {
        return disposed
      }
    }
  }
}
