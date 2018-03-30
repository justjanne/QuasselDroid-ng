package de.kuschku.quasseldroid.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.BackgroundColorSpan

sealed class IrcBackgroundColorSpan<T : IrcBackgroundColorSpan<T>>(@ColorInt color: Int) :
  BackgroundColorSpan(color), Copyable<T> {
  class MIRC(val mircColor: Int, @ColorInt color: Int) :
    IrcBackgroundColorSpan<MIRC>(color), Copyable<MIRC> {
    override fun copy() = MIRC(mircColor, backgroundColor)
  }

  class HEX(@ColorInt color: Int) :
    IrcBackgroundColorSpan<HEX>(color), Copyable<HEX> {
    override fun copy() = HEX(backgroundColor)
  }
}
