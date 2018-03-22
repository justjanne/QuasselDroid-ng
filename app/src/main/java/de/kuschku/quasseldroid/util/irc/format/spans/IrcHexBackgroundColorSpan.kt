package de.kuschku.quasseldroid.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.BackgroundColorSpan

class IrcHexBackgroundColorSpan(@ColorInt color: Int) : BackgroundColorSpan(color),
                                                        Copyable<IrcHexBackgroundColorSpan> {
  override fun copy() = IrcHexBackgroundColorSpan(backgroundColor)
}
