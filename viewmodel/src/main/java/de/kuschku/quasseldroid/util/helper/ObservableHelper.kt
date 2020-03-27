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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.util.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import de.kuschku.libquassel.util.compatibility.HandlerService
import io.reactivex.*
import io.reactivex.schedulers.Schedulers

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST,
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> =
  LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler).toFlowable(strategy))

inline fun <T> Maybe<T>.toLiveData(
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> =
  LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler).toFlowable())

inline fun <T> Flowable<T>.toLiveData(
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> = LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler))
