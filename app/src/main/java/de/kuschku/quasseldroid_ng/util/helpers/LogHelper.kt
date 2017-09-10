package de.kuschku.quasseldroid_ng.util.helpers

import android.util.Log
import de.kuschku.quasseldroid_ng.BuildConfig

class Logger {
  companion object {
    var handler: ((String, String?, Throwable?) -> Unit)? = null
  }
}

inline fun Logger.Companion.loggable(tag: String, level: Int, f: Logger.Companion.() -> Unit) {
  if (BuildConfig.DEBUG || Log.isLoggable(tag, level) || true) {
    Logger.Companion.f()
  }
}

/* VERBOSE */

inline fun Logger.Companion.verbose(tag: String, msg: String)
  = loggable(tag, Log.VERBOSE) {
  Log.v(tag, msg)
  handler?.invoke(tag, msg, null)
}

inline fun Logger.Companion.verbose(tag: String, msg: () -> String)
  = loggable(tag, Log.VERBOSE) {
  val msg1 = msg()
  Log.v(tag, msg1)
  handler?.invoke(tag, msg1, null)
}

inline fun Logger.Companion.verbose(tag: String, msg: String, tr: Throwable)
  = loggable(tag, Log.VERBOSE) {
  Log.v(tag, msg, tr)
  handler?.invoke(tag, msg, tr)
}

inline fun Logger.Companion.verbose(tag: String, msg: () -> String, tr: Throwable)
  = loggable(tag, Log.VERBOSE) {
  val msg1 = msg()
  Log.v(tag, msg1, tr)
  handler?.invoke(tag, msg1, tr)
}

inline fun Logger.Companion.verbose(tag: String, msg: String, tr: () -> Throwable)
  = loggable(tag, Log.VERBOSE) {
  val tr1 = tr()
  Log.v(tag, msg, tr1)
  handler?.invoke(tag, msg, tr1)
}

inline fun Logger.Companion.verbose(tag: String, msg: () -> String, tr: () -> Throwable)
  = loggable(tag, Log.VERBOSE) {
  val tr1 = tr()
  val msg1 = msg()
  Log.v(tag, msg1, tr1)
  handler?.invoke(tag, msg1, tr1)
}


/* DEBUG */

inline fun Logger.Companion.debug(tag: String, msg: String)
  = loggable(tag, Log.DEBUG) {
  Log.d(tag, msg)
  handler?.invoke(tag, msg, null)
}

inline fun Logger.Companion.debug(tag: String, msg: () -> String)
  = loggable(tag, Log.DEBUG) {
  val msg1 = msg()
  Log.d(tag, msg1)
  handler?.invoke(tag, msg1, null)
}

inline fun Logger.Companion.debug(tag: String, msg: String, tr: Throwable)
  = loggable(tag, Log.DEBUG) {
  Log.d(tag, msg, tr)
  handler?.invoke(tag, msg, tr)
}

inline fun Logger.Companion.debug(tag: String, msg: () -> String, tr: Throwable)
  = loggable(tag, Log.DEBUG) {
  val msg1 = msg()
  Log.d(tag, msg1, tr)
  handler?.invoke(tag, msg1, tr)
}

inline fun Logger.Companion.debug(tag: String, msg: String, tr: () -> Throwable)
  = loggable(tag, Log.DEBUG) {
  val tr1 = tr()
  Log.d(tag, msg, tr1)
  handler?.invoke(tag, msg, tr1)
}

inline fun Logger.Companion.debug(tag: String, msg: () -> String, tr: () -> Throwable)
  = loggable(tag, Log.DEBUG) {
  val tr1 = tr()
  val msg1 = msg()
  Log.d(tag, msg1, tr1)
  handler?.invoke(tag, msg1, tr1)
}


/* INFO */

inline fun Logger.Companion.info(tag: String, msg: String)
  = loggable(tag, Log.INFO) {
  Log.i(tag, msg)
  handler?.invoke(tag, msg, null)
}

inline fun Logger.Companion.info(tag: String, msg: () -> String)
  = loggable(tag, Log.INFO) {
  val msg1 = msg()
  Log.i(tag, msg1)
  handler?.invoke(tag, msg1, null)
}

inline fun Logger.Companion.info(tag: String, msg: String, tr: Throwable)
  = loggable(tag, Log.INFO) {
  Log.i(tag, msg, tr)
  handler?.invoke(tag, msg, tr)
}

inline fun Logger.Companion.info(tag: String, msg: () -> String, tr: Throwable)
  = loggable(tag, Log.INFO) {
  val msg1 = msg()
  Log.i(tag, msg1, tr)
  handler?.invoke(tag, msg1, tr)
}

inline fun Logger.Companion.info(tag: String, msg: String, tr: () -> Throwable)
  = loggable(tag, Log.INFO) {
  val tr1 = tr()
  Log.i(tag, msg, tr1)
  handler?.invoke(tag, msg, tr1)
}

inline fun Logger.Companion.info(tag: String, msg: () -> String, tr: () -> Throwable)
  = loggable(tag, Log.INFO) {
  val tr1 = tr()
  val msg1 = msg()
  Log.i(tag, msg1, tr1)
  handler?.invoke(tag, msg1, tr1)
}

/* WARN */

inline fun Logger.Companion.warn(tag: String, msg: String)
  = loggable(tag, Log.WARN) {
  Log.w(tag, msg)
  handler?.invoke(tag, msg, null)
}

inline fun Logger.Companion.warn(tag: String, msg: () -> String)
  = loggable(tag, Log.WARN) {
  val msg1 = msg()
  Log.w(tag, msg1)
  handler?.invoke(tag, msg1, null)
}

inline fun Logger.Companion.warn(tag: String, msg: String, tr: Throwable)
  = loggable(tag, Log.WARN) {
  Log.w(tag, msg, tr)
  handler?.invoke(tag, msg, tr)
}

inline fun Logger.Companion.warn(tag: String, msg: () -> String, tr: Throwable)
  = loggable(tag, Log.WARN) {
  val msg1 = msg()
  Log.w(tag, msg1, tr)
  handler?.invoke(tag, msg1, tr)
}

inline fun Logger.Companion.warn(tag: String, msg: String, tr: () -> Throwable)
  = loggable(tag, Log.WARN) {
  val tr1 = tr()
  Log.w(tag, msg, tr1)
  handler?.invoke(tag, msg, tr1)
}

inline fun Logger.Companion.warn(tag: String, msg: () -> String, tr: () -> Throwable)
  = loggable(tag, Log.WARN) {
  val tr1 = tr()
  val msg1 = msg()
  Log.w(tag, msg1, tr1)
  handler?.invoke(tag, msg1, tr1)
}

/* ERROR */

inline fun Logger.Companion.error(tag: String, msg: String)
  = loggable(tag, Log.ERROR) {
  Log.e(tag, msg)
  handler?.invoke(tag, msg, null)
}

inline fun Logger.Companion.error(tag: String, msg: () -> String)
  = loggable(tag, Log.ERROR) {
  val msg1 = msg()
  Log.e(tag, msg1)
  handler?.invoke(tag, msg1, null)
}

inline fun Logger.Companion.error(tag: String, msg: String, tr: Throwable)
  = loggable(tag, Log.ERROR) {
  Log.e(tag, msg, tr)
  handler?.invoke(tag, msg, tr)
}

inline fun Logger.Companion.error(tag: String, msg: () -> String, tr: Throwable)
  = loggable(tag, Log.ERROR) {
  val msg1 = msg()
  Log.e(tag, msg1, tr)
  handler?.invoke(tag, msg1, tr)
}

inline fun Logger.Companion.error(tag: String, msg: String, tr: () -> Throwable)
  = loggable(tag, Log.ERROR) {
  val tr1 = tr()
  Log.e(tag, msg, tr1)
  handler?.invoke(tag, msg, tr1)
}

inline fun Logger.Companion.error(tag: String, msg: () -> String, tr: () -> Throwable)
  = loggable(tag, Log.ERROR) {
  val tr1 = tr()
  val msg1 = msg()
  Log.e(tag, msg1, tr1)
  handler?.invoke(tag, msg1, tr1)
}
