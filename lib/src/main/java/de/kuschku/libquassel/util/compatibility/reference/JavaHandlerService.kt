/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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
