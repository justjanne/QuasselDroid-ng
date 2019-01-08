/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Printer

class AndroidHandlerThread(name: String) : HandlerThread(name) {
  @Volatile
  private var handler: Handler? =
    null

  private fun started(): AndroidHandlerThread {
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

  fun handleMessage(msg: Message) =
    started().let { handler?.handleMessage(msg) }
    ?: throw RuntimeException("Thread not started")

  fun dispatchMessage(msg: Message) =
    started().let { handler?.dispatchMessage(msg) }
    ?: throw RuntimeException("Thread not started")

  fun getMessageName(message: Message): String =
    started().let { handler?.getMessageName(message) }
    ?: throw RuntimeException("Thread not started")

  fun obtainMessage(): Message =
    started().let { handler?.obtainMessage() }
    ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int): Message =
    started().let { handler?.obtainMessage(what) }
    ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, obj: Any): Message =
    started().let { handler?.obtainMessage(what, obj) }
    ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, arg1: Int, arg2: Int): Message =
    started().let { handler?.obtainMessage(what, arg1, arg2) }
    ?: throw RuntimeException("Thread not started")

  fun obtainMessage(what: Int, arg1: Int, arg2: Int, obj: Any): Message =
    started().let { handler?.obtainMessage(what, arg1, arg2, obj) }
    ?: throw RuntimeException("Thread not started")

  fun post(r: () -> Unit): Boolean =
    started().let { handler?.post(r) }
    ?: throw RuntimeException("Thread not started")

  fun postAtTime(r: () -> Unit, uptimeMillis: Long): Boolean =
    started().let { handler?.postAtTime(r, uptimeMillis) }
    ?: throw RuntimeException("Thread not started")

  fun postAtTime(r: () -> Unit, token: Any, uptimeMillis: Long): Boolean =
    started().let { handler?.postAtTime(r, token, uptimeMillis) }
    ?: throw RuntimeException("Thread not started")

  fun postDelayed(r: () -> Unit, delayMillis: Long): Boolean =
    started().let { handler?.postDelayed(r, delayMillis) }
    ?: throw RuntimeException("Thread not started")

  fun postAtFrontOfQueue(r: () -> Unit): Boolean =
    started().let { handler?.postAtFrontOfQueue(r) }
    ?: throw RuntimeException("Thread not started")

  fun removeCallbacks(r: () -> Unit) =
    started().let { handler?.removeCallbacks(r) }
    ?: throw RuntimeException("Thread not started")

  fun removeCallbacks(r: () -> Unit, token: Any) =
    started().let { handler?.removeCallbacks(r, token) }
    ?: throw RuntimeException("Thread not started")

  fun sendMessage(msg: Message): Boolean =
    started().let { handler?.sendMessage(msg) }
    ?: throw RuntimeException("Thread not started")

  fun sendEmptyMessage(what: Int): Boolean =
    started().let { handler?.sendEmptyMessage(what) }
    ?: throw RuntimeException("Thread not started")

  fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean =
    started().let { handler?.sendEmptyMessageDelayed(what, delayMillis) }
    ?: throw RuntimeException(
      "Thread not started"
    )

  fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean =
    started().let { handler?.sendEmptyMessageAtTime(what, uptimeMillis) }
    ?: throw RuntimeException(
      "Thread not started"
    )

  fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean =
    started().let { handler?.sendMessageDelayed(msg, delayMillis) }
    ?: throw RuntimeException("Thread not started")

  fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean =
    started().let { handler?.sendMessageAtTime(msg, uptimeMillis) }
    ?: throw RuntimeException("Thread not started")

  fun sendMessageAtFrontOfQueue(msg: Message): Boolean =
    started().let { handler?.sendMessageAtFrontOfQueue(msg) }
    ?: throw RuntimeException("Thread not started")

  fun removeMessages(what: Int) =
    started().let { handler?.removeMessages(what) }
    ?: throw RuntimeException("Thread not started")

  fun removeMessages(what: Int, `object`: Any) =
    started().let { handler?.removeMessages(what, `object`) }
    ?: throw RuntimeException("Thread not started")

  fun removeCallbacksAndMessages(token: Any) =
    started().let { handler?.removeCallbacksAndMessages(token) }
    ?: throw RuntimeException("Thread not started")

  fun hasMessages(what: Int): Boolean =
    started().let { handler?.hasMessages(what) }
    ?: throw RuntimeException("Thread not started")

  fun hasMessages(what: Int, `object`: Any): Boolean =
    started().let { handler?.hasMessages(what, `object`) }
    ?: throw RuntimeException("Thread not started")

  val handlerLooper: Looper
    get()

    =
      started().let { handler?.looper }
      ?: throw RuntimeException("Thread not started")

  fun dump(pw: Printer, prefix: String) =
    started().let { handler?.dump(pw, prefix) }
    ?: throw RuntimeException("Thread not started")

  override fun toString(): String =
    started().let { handler?.toString() }
    ?: throw RuntimeException("Thread not started")
}
