package de.kuschku.quasseldroid_ng.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Printer

class AndroidHandlerThread(name: String) : HandlerThread(name) {
  @Volatile
  private var handler: Handler? = null

  fun started(): AndroidHandlerThread {
    onCreate()
    return this
  }

  fun onCreate() {
    if (handler == null) {
      synchronized(this@AndroidHandlerThread) {
        if (handler == null) {
          start()
          handler = Handler(looper)
        }
      }
    }
  }

  fun onDestroy() {
    if (handler != null) {
      post {
        quit()
        if (handler != null) {
          synchronized(this@AndroidHandlerThread) {
            if (handler != null) {
              handler = null
            }
          }
        }
      }
    }
  }

  fun handleMessage(msg: Message)
    = handler?.handleMessage(msg) ?: throw RuntimeException("Thread not started")

  fun dispatchMessage(msg: Message)
    = handler?.dispatchMessage(msg) ?: throw RuntimeException("Thread not started")

  fun getMessageName(message: Message): String
    = handler?.getMessageName(message) ?: throw RuntimeException("Thread not started")

  fun obtainMessage(): Message
    = handler?.obtainMessage() ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int): Message
    = handler?.obtainMessage(what) ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, obj: Any): Message
    = handler?.obtainMessage(what, obj) ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, arg1: Int, arg2: Int): Message
    = handler?.obtainMessage(what, arg1, arg2) ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, arg1: Int, arg2: Int, obj: Any): Message
    = handler?.obtainMessage(what, arg1, arg2, obj) ?: throw RuntimeException("Thread not started")

  fun post(r: () -> Unit): Boolean
    = handler?.post(r) ?: throw RuntimeException("Thread not started")

  fun postAtTime(r: () -> Unit, uptimeMillis: Long): Boolean
    = handler?.postAtTime(r, uptimeMillis) ?: throw RuntimeException("Thread not started")

  fun postAtTime(r: () -> Unit, token: Any, uptimeMillis: Long): Boolean
    = handler?.postAtTime(r, token, uptimeMillis) ?: throw RuntimeException("Thread not started")

  fun postDelayed(r: () -> Unit, delayMillis: Long): Boolean
    = handler?.postDelayed(r, delayMillis) ?: throw RuntimeException("Thread not started")

  fun postAtFrontOfQueue(r: () -> Unit): Boolean
    = handler?.postAtFrontOfQueue(r) ?: throw RuntimeException("Thread not started")

  fun removeCallbacks(r: () -> Unit)
    = handler?.removeCallbacks(r) ?: throw RuntimeException("Thread not started")

  fun removeCallbacks(r: () -> Unit, token: Any)
    = handler?.removeCallbacks(r, token) ?: throw RuntimeException("Thread not started")

  fun sendMessage(msg: Message): Boolean
    = handler?.sendMessage(msg) ?: throw RuntimeException("Thread not started")

  fun sendEmptyMessage(what: Int): Boolean
    = handler?.sendEmptyMessage(what) ?: throw RuntimeException("Thread not started")

  fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean
    = handler?.sendEmptyMessageDelayed(what, delayMillis) ?: throw RuntimeException(
    "Thread not started"
  )

  fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean
    = handler?.sendEmptyMessageAtTime(what, uptimeMillis) ?: throw RuntimeException(
    "Thread not started"
  )

  fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean
    = handler?.sendMessageDelayed(msg, delayMillis) ?: throw RuntimeException("Thread not started")

  fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean
    = handler?.sendMessageAtTime(msg, uptimeMillis) ?: throw RuntimeException("Thread not started")

  fun sendMessageAtFrontOfQueue(msg: Message): Boolean
    = handler?.sendMessageAtFrontOfQueue(msg) ?: throw RuntimeException("Thread not started")

  fun removeMessages(what: Int)
    = handler?.removeMessages(what) ?: throw RuntimeException("Thread not started")

  fun removeMessages(what: Int, `object`: Any)
    = handler?.removeMessages(what, `object`) ?: throw RuntimeException("Thread not started")

  fun removeCallbacksAndMessages(token: Any)
    = handler?.removeCallbacksAndMessages(token) ?: throw RuntimeException("Thread not started")

  fun hasMessages(what: Int): Boolean
    = handler?.hasMessages(what) ?: throw RuntimeException("Thread not started")

  fun hasMessages(what: Int, `object`: Any): Boolean
    = handler?.hasMessages(what, `object`) ?: throw RuntimeException("Thread not started")

  val handlerLooper: Looper
    get() = handler?.looper ?: throw RuntimeException("Thread not started")

  fun dump(pw: Printer, prefix: String)
    = handler?.dump(pw, prefix) ?: throw RuntimeException("Thread not started")

  override fun toString(): String
    = handler?.toString() ?: throw RuntimeException("Thread not started")
}
