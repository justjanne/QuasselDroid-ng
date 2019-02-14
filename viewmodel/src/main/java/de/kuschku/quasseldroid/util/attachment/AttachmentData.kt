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

package de.kuschku.quasseldroid.util.attachment

import com.google.gson.annotations.SerializedName

data class AttachmentData(
  @SerializedName("from_url")
  val fromUrl: String?,
  @SerializedName("color")
  val color: String?,
  @SerializedName("author_name")
  val authorName: String?,
  @SerializedName("author_link")
  val authorLink: String?,
  @SerializedName("author_icon")
  val authorIcon: String?,
  @SerializedName("title")
  val title: String?,
  @SerializedName("title_link")
  val titleLink: String?,
  @SerializedName("text")
  val text: String?,
  @SerializedName("fields")
  val fields: List<AttachmentDataField>?,
  @SerializedName("image_url")
  val imageUrl: String?,
  @SerializedName("type")
  val type: String?,
  @SerializedName("player")
  val player: String?,
  @SerializedName("service_name")
  val serviceName: String?,
  @SerializedName("service_icon")
  val serviceIcon: String?,
  @SerializedName("ts")
  val timestamp: Int?
)
