package de.kuschku.libquassel.util.compatibility

interface HandlerService {
  fun parse(f: () -> Unit)
  fun write(f: () -> Unit)
  fun handle(f: () -> Unit)

  fun quit()

  var exceptionHandler: Thread.UncaughtExceptionHandler?
}
