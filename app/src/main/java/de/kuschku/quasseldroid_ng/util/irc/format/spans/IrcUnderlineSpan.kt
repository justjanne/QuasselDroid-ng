package de.kuschku.quasseldroid_ng.util.irc.format.spans

import android.text.style.UnderlineSpan

class IrcUnderlineSpan : UnderlineSpan(), Copyable<IrcUnderlineSpan> {
  override fun copy() = IrcUnderlineSpan()
}
