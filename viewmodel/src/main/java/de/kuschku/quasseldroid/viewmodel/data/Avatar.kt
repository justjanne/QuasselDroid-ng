/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

import java.io.Serializable

sealed class Avatar : Serializable {
  data class NativeAvatar(val url: String) : Avatar()
  data class IRCCloudAvatar(val url: String) : Avatar()
  data class LibravatarAvatar(val url: String) : Avatar()
  data class GravatarAvatar(val url: String) : Avatar()
  data class MatrixAvatar(val userId: String, val size: Int?) : Avatar()
}
