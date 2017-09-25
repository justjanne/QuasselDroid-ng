package de.kuschku.quasseldroid_ng.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import de.kuschku.libquassel.util.compatibility.HandlerService

class AndroidHandlerService : HandlerService {
  override fun parse(f: () -> Unit) {
    parseHandler.post(f)
  }

  override fun write(f: () -> Unit) {
    writeHandler.post(f)
  }

  override fun handle(f: () -> Unit) {
    backendHandler.post(f)
  }

  private val parseThread = HandlerThread("parse", Process.THREAD_PRIORITY_DISPLAY)
  private val writeThread = HandlerThread("write", Process.THREAD_PRIORITY_BACKGROUND)
  private val backendThread = HandlerThread("backend", Process.THREAD_PRIORITY_DISPLAY)

  private val parseHandler: Handler
  private val writeHandler: Handler
  private val backendHandler: Handler

  private val internalExceptionHandler = Thread.UncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
    exceptionHandler?.uncaughtException(thread, throwable)
  }

  init {
    parseThread.uncaughtExceptionHandler = internalExceptionHandler
    writeThread.uncaughtExceptionHandler = internalExceptionHandler
    backendThread.uncaughtExceptionHandler = internalExceptionHandler

    parseThread.start()
    writeThread.start()
    backendThread.start()

    parseHandler = Handler(parseThread.looper)
    writeHandler = Handler(writeThread.looper)
    backendHandler = Handler(backendThread.looper)
  }

  override var exceptionHandler: Thread.UncaughtExceptionHandler? = null

  override fun quit() {
    parseThread.quit()
    writeThread.quit()
    backendThread.quit()
  }
}
