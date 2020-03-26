/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.irc.format.spans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.ui.chat.ChatActivity

class ChannelLinkSpan(
  private val networkId: NetworkId,
  private val text: String,
  private val highlight: Boolean
) : ClickableSpan() {
  override fun updateDrawState(ds: TextPaint) {
    if (!highlight) ds.color = ds.linkColor
    ds.isUnderlineText = true
  }

  override fun onClick(widget: View) {
    ChatActivity.launch(
      widget.context,
      networkId = networkId,
      channel = text
    )
  }
}
