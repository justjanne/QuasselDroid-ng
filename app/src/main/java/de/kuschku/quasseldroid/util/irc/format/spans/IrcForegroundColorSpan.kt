package de.kuschku.quasseldroid.util.irc.format.spans

import android.support.annotation.ColorInt
import android.text.style.ForegroundColorSpan

sealed class IrcForegroundColorSpan<T : IrcForegroundColorSpan<T>>(@ColorInt color: Int) :
  ForegroundColorSpan(color), Copyable<T> {
  class MIRC(private val mircColor: Int, @ColorInt color: Int) :
    IrcForegroundColorSpan<MIRC>(color), Copyable<MIRC> {
    override fun copy() = MIRC(mircColor, foregroundColor)
  }

  class HEX(@ColorInt color: Int) :
    IrcForegroundColorSpan<HEX>(color), Copyable<HEX> {
    override fun copy() = HEX(foregroundColor)
  }
}
