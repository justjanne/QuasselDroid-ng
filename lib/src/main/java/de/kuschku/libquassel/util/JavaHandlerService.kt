package de.kuschku.libquassel.util

import java.util.concurrent.Executors

class JavaHandlerService : HandlerService {
  private val parseExecutor = Executors.newSingleThreadExecutor()
  private val writeExecutor = Executors.newSingleThreadExecutor()
  private val backendExecutor = Executors.newSingleThreadExecutor()

  override fun parse(f: () -> Unit) {
    parseExecutor.submit {
      try {
        f()
      } catch (e: Throwable) {
        exceptionHandler?.uncaughtException(Thread.currentThread(), e)
      }
    }
  }

  override fun write(f: () -> Unit) {
    writeExecutor.submit {
      try {
        f()
      } catch (e: Throwable) {
        exceptionHandler?.uncaughtException(Thread.currentThread(), e)
      }
    }
  }

  override fun handle(f: () -> Unit) {
    backendExecutor.submit {
      try {
        f()
      } catch (e: Throwable) {
        exceptionHandler?.uncaughtException(Thread.currentThread(), e)
      }
    }
  }

  override fun quit() {
    parseExecutor.shutdownNow()
    writeExecutor.shutdownNow()
    backendExecutor.shutdownNow()
  }

  override var exceptionHandler: Thread.UncaughtExceptionHandler? = null
}
