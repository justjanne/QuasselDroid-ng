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

package de.kuschku.quasseldroid.ui.chat.archive

import de.kuschku.quasseldroid.viewmodel.data.BufferListItem

sealed class ArchiveListItem(val type: Type) {
  data class Header(
    val title: String,
    val content: String
  ) : ArchiveListItem(Type.HEADER)

  data class Placeholder(
    val content: String
  ) : ArchiveListItem(Type.PLACEHOLDER)

  data class Buffer(
    val item: BufferListItem
  ) : ArchiveListItem(Type.BUFFER)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ArchiveListItem) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  enum class Type(val value: UByte) {
    HEADER(0u),
    PLACEHOLDER(1u),
    BUFFER(2u);

    companion object {
      private val map = values().associateBy { it.value }
      fun of(value: UByte) = map[value]
    }
  }
}
