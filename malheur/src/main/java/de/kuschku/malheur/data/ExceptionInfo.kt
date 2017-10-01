package de.kuschku.malheur.data

data class ExceptionInfo(
  val type: String?,
  val message: String?,
  val localizedMessage: String?,
  val stackTrace: List<TraceElement>?,
  val cause: ExceptionInfo?
) {
  constructor(throwable: Throwable) : this(
    type = throwable.javaClass.canonicalName,
    message = throwable.message,
    localizedMessage = throwable.localizedMessage,
    stackTrace = throwable.stackTrace?.map(::TraceElement),
    cause = throwable.cause?.let(::ExceptionInfo)
  )
}
