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
    stackTrace = throwable.stackTrace.map(::TraceElement),
    cause = throwable.cause?.let(::ExceptionInfo)
  )
}
