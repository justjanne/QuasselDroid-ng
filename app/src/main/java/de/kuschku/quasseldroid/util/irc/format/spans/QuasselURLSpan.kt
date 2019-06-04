/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.TextPaint
import android.text.style.URLSpan
import android.util.Log
import android.view.View

class QuasselURLSpan(text: String, private val highlight: Boolean) : URLSpan(text) {
  override fun updateDrawState(ds: TextPaint?) {
    if (ds != null) {
      if (!highlight) ds.color = ds.linkColor
      ds.isUnderlineText = true
    }
  }

  override fun onClick(widget: View) {
    try {
      widget.context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
      })
    } catch (e: ActivityNotFoundException) {
      Log.w("QuasselURLSpan", "Actvity was not found for $url")
    }
  }
}
