package de.kuschku.quasseldroid.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.BackgroundColorSpan

class IrcBackgroundColorSpan(
  val mircColor: Int,
  @ColorInt color: Int
) : BackgroundColorSpan(color), Copyable<IrcBackgroundColorSpan> {
  override fun copy() = IrcBackgroundColorSpan(mircColor, backgroundColor)
}
