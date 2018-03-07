package de.kuschku.quasseldroid_ng.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.ForegroundColorSpan

class IrcHexForegroundColorSpan(@ColorInt color: Int) : ForegroundColorSpan(color),
                                                        Copyable<IrcHexForegroundColorSpan> {
  override fun copy() = IrcHexForegroundColorSpan(foregroundColor)
}
