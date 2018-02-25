package de.kuschku.quasseldroid_ng.util.irc.format.spans

import android.text.style.TypefaceSpan

class IrcMonospaceSpan : TypefaceSpan("monospace"), Copyable<IrcMonospaceSpan> {
  override fun copy() = IrcMonospaceSpan()
}
