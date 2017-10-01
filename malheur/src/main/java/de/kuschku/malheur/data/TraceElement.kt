package de.kuschku.malheur.data

data class TraceElement(
  val className: String?,
  val methodName: String?,
  val fileName: String?,
  val lineNumber: Int?,
  val isNative: Boolean?
) {
  constructor(element: StackTraceElement) : this(
    className = element.className,
    methodName = element.methodName,
    fileName = element.fileName,
    lineNumber = element.lineNumber,
    isNative = element.isNativeMethod
  )
}
