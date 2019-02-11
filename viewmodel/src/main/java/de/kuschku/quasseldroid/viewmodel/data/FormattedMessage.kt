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

package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable
import de.kuschku.quasseldroid.persistence.models.MessageData

class FormattedMessage(
  val original: MessageData,
  val time: CharSequence,
  val dayChange: CharSequence? = null,
  val name: CharSequence? = null,
  val content: CharSequence? = null,
  val combined: CharSequence,
  val fallbackDrawable: Drawable? = null,
  val realName: CharSequence? = null,
  val avatarUrls: List<Avatar> = emptyList(),
  val urls: List<String> = emptyList(),
  val hasDayChange: Boolean,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean,
  val hasSpoilers: Boolean
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FormattedMessage

    if (original != other.original) return false
    if (time != other.time) return false
    if (dayChange != other.dayChange) return false
    if (name != other.name) return false
    if (content != other.content) return false
    if (combined != other.combined) return false
    if (realName != other.realName) return false
    if (avatarUrls != other.avatarUrls) return false
    if (urls != other.urls) return false
    if (hasDayChange != other.hasDayChange) return false
    if (isSelected != other.isSelected) return false
    if (isExpanded != other.isExpanded) return false
    if (isMarkerLine != other.isMarkerLine) return false

    return true
  }

  override fun hashCode(): Int {
    var result = original.hashCode()
    result = 31 * result + time.hashCode()
    result = 31 * result + (dayChange?.hashCode() ?: 0)
    result = 31 * result + (name?.hashCode() ?: 0)
    result = 31 * result + (content?.hashCode() ?: 0)
    result = 31 * result + combined.hashCode()
    result = 31 * result + (realName?.hashCode() ?: 0)
    result = 31 * result + avatarUrls.hashCode()
    result = 31 * result + urls.hashCode()
    result = 31 * result + hasDayChange.hashCode()
    result = 31 * result + isSelected.hashCode()
    result = 31 * result + isExpanded.hashCode()
    result = 31 * result + isMarkerLine.hashCode()
    return result
  }
}
