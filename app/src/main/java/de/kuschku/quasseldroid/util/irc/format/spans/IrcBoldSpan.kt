package de.kuschku.quasseldroid.util.irc.format.spans

import android.graphics.Typeface
import android.text.style.StyleSpan

class IrcBoldSpan : StyleSpan(Typeface.BOLD), Copyable<IrcBoldSpan> {
  override fun copy() = IrcBoldSpan()
}
