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
import com.google.gson.Gson
import de.kuschku.quasseldroid.util.emoji.EmojiHandler
import de.kuschku.quasseldroid.util.emoji.EmojiProvider
import de.kuschku.quasseldroid.util.helper.fromJsonList
import java.io.IOException

class AndroidEmojiProvider(context: Context, gson: Gson) : EmojiProvider {
  override val emoji: List<EmojiHandler.Emoji> = try {
    context.assets.open("emoji.json").use {
      gson.fromJsonList(it.bufferedReader(Charsets.UTF_8))
    }
  } catch (e: IOException) {
    throw IllegalStateException("emoji.json missing from assets.", e)
  }

  override val shortcodes: Map<String, EmojiHandler.Emoji> = emoji.flatMap { entry ->
    entry.shortcodes.map { Pair(it, entry) }
  }.toMap()
}
