package de.kuschku.quasseldroid_ng.util.irc.format.spans

import android.text.style.StrikethroughSpan

class IrcStrikethroughSpan : StrikethroughSpan(), Copyable<IrcStrikethroughSpan> {
  override fun copy() = IrcStrikethroughSpan()
}
