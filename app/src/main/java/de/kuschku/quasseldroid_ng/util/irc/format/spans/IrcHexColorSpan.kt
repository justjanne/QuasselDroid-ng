package de.kuschku.quasseldroid_ng.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.ForegroundColorSpan

class IrcHexColorSpan(@ColorInt color: Int) : ForegroundColorSpan(color),
                                              Copyable<IrcHexColorSpan> {
  override fun copy() = IrcHexColorSpan(foregroundColor)
}
