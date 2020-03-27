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

package de.kuschku.quasseldroid.util

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorInt
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.ui.drawable.TextDrawable
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper.Companion.IGNORED_CHARS
import javax.inject.Inject

class ColorContext @Inject constructor(
  context: Context,
  private val messageSettings: MessageSettings
) {
  private val senderColors = context.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(length()) {
      getColor(it, 0)
    }
  }

  private val selfColor = context.theme.styledAttributes(R.attr.colorForegroundSecondary) {
    getColor(0, 0)
  }

  private val textColor = context.theme.styledAttributes(R.attr.colorBackground) {
    getColor(0, 0)
  }

  val colorAccent = context.theme.styledAttributes(R.attr.colorAccent) {
    getColor(0, 0)
  }

  val colorAway = context.theme.styledAttributes(R.attr.colorAway) {
    getColor(0, 0)
  }

  val avatarRadius = context.resources.getDimensionPixelSize(R.dimen.avatar_radius)
  val avatarSize = context.resources.getDimensionPixelSize(R.dimen.avatar_size_buffer)

  fun prepareTextDrawable(@ColorInt textColor: Int = this.textColor): TextDrawable.IShapeBuilder =
    TextDrawable.builder()
      .beginConfig()
      .textColor(setAlpha(textColor, 0x8A))
      .useFont(Typeface.DEFAULT_BOLD)
      .endConfig()

  fun buildTextDrawable(initial: String, @ColorInt backgroundColor: Int): TextDrawable =
    prepareTextDrawable(textColor).let {
      if (messageSettings.squareAvatars) it.buildRoundRect(initial, backgroundColor, avatarRadius)
      else it.buildRound(initial, backgroundColor)
    }

  fun buildTextDrawable(nickName: String, self: Boolean): TextDrawable {
    val senderColorIndex = SenderColorUtil.senderColor(nickName)
    val rawInitial = nickName.trimStart(*IGNORED_CHARS).firstOrNull()
                     ?: nickName.firstOrNull()
    val initial = rawInitial?.toUpperCase().toString()
    val senderColor = if (self) selfColor else senderColors[senderColorIndex]

    return buildTextDrawable(initial, senderColor)
  }

  companion object {
    @ColorInt
    private fun setAlpha(@ColorInt color: Int, alpha: Int) = (color and 0xFFFFFF) or (alpha shl 24)
  }
}
