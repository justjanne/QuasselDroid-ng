package de.kuschku.libquassel.util.compatibility

import io.reactivex.Scheduler

interface HandlerService {
  val scheduler: Scheduler

  var exceptionHandler: Thread.UncaughtExceptionHandler?

  fun serialize(f: () -> Unit)
  fun deserialize(f: () -> Unit)
  fun write(f: () -> Unit)
  fun backend(f: () -> Unit)
  fun backendDelayed(delayMillis: Long, f: () -> Unit)

  fun quit()
}
