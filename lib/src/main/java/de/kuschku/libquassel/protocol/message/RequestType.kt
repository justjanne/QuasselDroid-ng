/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.message

enum class RequestType(val value: Int) {
  Invalid(0),
  Sync(1),
  RpcCall(2),
  InitRequest(3),
  InitData(4),
  HeartBeat(5),
  HeartBeatReply(6);

  companion object {
    private val byId = enumValues<RequestType>().associateBy(
      RequestType::value
    )

    fun of(value: Int) = byId[value] ?: Invalid
  }
}
