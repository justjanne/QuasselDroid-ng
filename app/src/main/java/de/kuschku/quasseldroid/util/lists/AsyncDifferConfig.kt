/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.quasseldroid.util.lists

import android.os.Handler
import android.os.Looper
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.DiffUtil

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Configuration object for [ListAdapter], [AsyncListDiffer], and similar
 * background-thread list diffing adapter logic.
 *
 *
 * At minimum, defines item diffing behavior with a [DiffUtil.ItemCallback], used to compute
 * item differences to pass to a RecyclerView adapter.
 *
 * @param <T> Type of items in the lists, and being compared.
</T> */
class AsyncDifferConfig<T> private constructor(
  @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  /** @hide
   */
  val mainThreadExecutor: Executor,
  val backgroundThreadExecutor: Executor,
  val diffCallback: DiffUtil.ItemCallback<T>) {

  /**
   * Builder class for [AsyncDifferConfig].
   *
   * @param <T>
  </T> */
  class Builder<T>(private val mDiffCallback: DiffUtil.ItemCallback<T>) {
    private var mMainThreadExecutor: Executor? = null
    private var mBackgroundThreadExecutor: Executor? = null

    /**
     * If provided, defines the main thread executor used to dispatch adapter update
     * notifications on the main thread.
     *
     *
     * If not provided, it will default to the main thread.
     *
     * @param executor The executor which can run tasks in the UI thread.
     * @return this
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun setMainThreadExecutor(executor: Executor): Builder<T> {
      mMainThreadExecutor = executor
      return this
    }

    /**
     * If provided, defines the background executor used to calculate the diff between an old
     * and a new list.
     *
     *
     * If not provided, defaults to two thread pool executor, shared by all ListAdapterConfigs.
     *
     * @param executor The background executor to run list diffing.
     * @return this
     */
    fun setBackgroundThreadExecutor(executor: Executor): Builder<T> {
      mBackgroundThreadExecutor = executor
      return this
    }

    private class MainThreadExecutor : Executor {
      internal val mHandler = Handler(Looper.getMainLooper())
      override fun execute(command: Runnable) {
        mHandler.post(command)
      }
    }

    /**
     * Creates a [AsyncListDiffer] with the given parameters.
     *
     * @return A new AsyncDifferConfig.
     */
    fun build(): AsyncDifferConfig<T> {
      if (mMainThreadExecutor == null) {
        mMainThreadExecutor = sMainThreadExecutor
      }
      if (mBackgroundThreadExecutor == null) {
        synchronized(sExecutorLock) {
          if (sDiffExecutor == null) {
            sDiffExecutor = Executors.newFixedThreadPool(2)
          }
        }
        mBackgroundThreadExecutor = sDiffExecutor
      }
      return AsyncDifferConfig(
        mMainThreadExecutor!!,
        mBackgroundThreadExecutor!!,
        mDiffCallback)
    }

    companion object {

      // TODO: remove the below once supportlib has its own appropriate executors
      private val sExecutorLock = Any()
      private var sDiffExecutor: Executor? = null

      // TODO: use MainThreadExecutor from supportlib once one exists
      private val sMainThreadExecutor = MainThreadExecutor()
    }
  }
}
