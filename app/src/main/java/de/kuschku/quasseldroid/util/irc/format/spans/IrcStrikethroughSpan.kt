package de.kuschku.quasseldroid.util.irc.format.spans

import android.text.style.StrikethroughSpan

class IrcStrikethroughSpan : StrikethroughSpan(), Copyable<IrcStrikethroughSpan> {
  override fun copy() = IrcStrikethroughSpan()
}
