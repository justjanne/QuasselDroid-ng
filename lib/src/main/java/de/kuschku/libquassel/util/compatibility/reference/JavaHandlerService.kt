package de.kuschku.libquassel.util.compatibility.reference

import de.kuschku.libquassel.util.compatibility.HandlerService
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class JavaHandlerService : HandlerService {
  override val scheduler = Schedulers.computation()

  override fun backendDelayed(delayMillis: Long, f: () -> Unit) = backend(f)

  private val serializeExecutor = Executors.newSingleThreadExecutor()
  private val deserializeExecutor = Executors.newSingleThreadExecutor()
  private val writeExecutor = Executors.newSingleThreadExecutor()
  private val backendExecutor = Executors.newSingleThreadExecutor()

  override fun serialize(f: () -> Unit) {
    serializeExecutor.submit {
      try {
        f()
      } catch (e: Throwable) {
        exceptionHandler?.uncaughtException(Thread.currentThread(), e)
      }
    }
  }

  override fun deserialize(f: () -> Unit) {
    deserializeExecutor.submit {
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

  override fun backend(f: () -> Unit) {
    backendExecutor.submit {
      try {
        f()
      } catch (e: Throwable) {
        exceptionHandler?.uncaughtException(Thread.currentThread(), e)
      }
    }
  }

  override fun quit() {
    serializeExecutor.shutdownNow()
    writeExecutor.shutdownNow()
    backendExecutor.shutdownNow()
  }

  override var exceptionHandler: Thread.UncaughtExceptionHandler? = null
}
