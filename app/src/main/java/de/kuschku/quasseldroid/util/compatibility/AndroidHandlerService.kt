package de.kuschku.quasseldroid.util.compatibility

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import de.kuschku.libquassel.util.compatibility.HandlerService

class AndroidHandlerService : HandlerService {
  override fun serialize(f: () -> Unit) {
    serializeHandler.post(f)
  }

  override fun deserialize(f: () -> Unit) {
    serializeHandler.post(f)
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
  }

  override var exceptionHandler: Thread.UncaughtExceptionHandler? = null
    get() = field ?: Thread.getDefaultUncaughtExceptionHandler()

  override fun quit() {
    serializeThread.quit()
    deserializeThread.quit()
    writeThread.quit()
    backendThread.quit()
  }
}
