/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2023 Janne Mareike Koschinski
 * Copyright (c) 2023 The Quassel Project
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

import android.content.Context
import de.kuschku.quasseldroid.util.emoji.EmojiHandler
import de.kuschku.quasseldroid.util.emoji.EmojiProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.DecodeSequenceMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import java.io.IOException

@OptIn(ExperimentalSerializationApi::class)
class AndroidEmojiProvider(context: Context) : EmojiProvider {
  override val emoji: List<EmojiHandler.Emoji> = try {
    context.assets.open("emoji.json").use {
      Json.decodeToSequence<EmojiHandler.Emoji>(it, DecodeSequenceMode.ARRAY_WRAPPED).toList()
    }
  } catch (e: IOException) {
    throw IllegalStateException("emoji.json missing from assets.", e)
  }

  override val shortcodes: Map<String, EmojiHandler.Emoji> = emoji.flatMap { entry ->
    entry.shortcodes.map { Pair(it, entry) }
  }.toMap()
}
