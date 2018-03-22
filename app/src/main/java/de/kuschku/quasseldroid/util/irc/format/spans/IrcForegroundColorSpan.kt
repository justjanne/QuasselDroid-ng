package de.kuschku.quasseldroid.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.ForegroundColorSpan

class IrcForegroundColorSpan(
  val mircColor: Int,
  @ColorInt color: Int
) : ForegroundColorSpan(color), Copyable<IrcForegroundColorSpan> {
  override fun copy() = IrcForegroundColorSpan(mircColor, foregroundColor)
}
