package de.kuschku.quasseldroid.util.irc.format.spans

import android.graphics.Typeface
import android.text.style.StyleSpan

class IrcItalicSpan : StyleSpan(Typeface.ITALIC), Copyable<IrcItalicSpan> {
  override fun copy() = IrcItalicSpan()
}
