package de.kuschku.quasseldroid.util

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorInt
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.EditorViewModel

class ColorContext(context: Context, private val messageSettings: MessageSettings) {
  val senderColors = context.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(length()) {
      getColor(it, 0)
    }
  }

  val selfColor = context.theme.styledAttributes(R.attr.colorForegroundSecondary) {
    getColor(0, 0)
  }

  val textColor = context.theme.styledAttributes(R.attr.colorBackground) {
    getColor(0, 0)
  }

  val radius = context.resources.getDimensionPixelSize(R.dimen.avatar_radius)


  fun prepareTextDrawable(@ColorInt textColor: Int = this.textColor) =
    TextDrawable.builder()
      .beginConfig()
      .textColor(setAlpha(textColor, 0x8A))
      .useFont(Typeface.DEFAULT_BOLD)
      .endConfig()

  fun buildTextDrawable(initial: String, @ColorInt backgroundColor: Int) =
    prepareTextDrawable(textColor).let {
      if (messageSettings.squareAvatars) it.buildRoundRect(initial, backgroundColor, radius)
      else it.buildRound(initial, backgroundColor)
    }

  fun buildTextDrawable(nickName: String, self: Boolean): TextDrawable {
    val senderColorIndex = SenderColorUtil.senderColor(nickName)
    val rawInitial = nickName.trimStart(*EditorViewModel.IGNORED_CHARS).firstOrNull()
                     ?: nickName.firstOrNull()
    val initial = rawInitial?.toUpperCase().toString()
    val senderColor = if (self) selfColor else senderColors[senderColorIndex]

    return buildTextDrawable(initial, senderColor)
  }

  @ColorInt
  private fun setAlpha(@ColorInt color: Int, alpha: Int) = (color and 0xFFFFFF) or (alpha shl 24)
}
