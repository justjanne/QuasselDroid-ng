package de.kuschku.libquassel.util.compatibility

interface HandlerService {
  fun serialize(f: () -> Unit)
  fun deserialize(f: () -> Unit)
  fun write(f: () -> Unit)
  fun backend(f: () -> Unit)
  fun backendDelayed(delayMillis: Long, f: () -> Unit)

  fun quit()

  var exceptionHandler: Thread.UncaughtExceptionHandler?
}
