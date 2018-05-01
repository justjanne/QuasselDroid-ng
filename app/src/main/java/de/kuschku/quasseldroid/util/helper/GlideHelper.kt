/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.helper

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.GlideRequest
import de.kuschku.quasseldroid.GlideRequests

fun GlideRequests.loadWithFallbacks(urls: List<String>): GlideRequest<Drawable>? {
  fun fold(url: String, fallback: RequestBuilder<Drawable>?): GlideRequest<Drawable> {
    return this.load(url).let {
      if (fallback != null) it.error(fallback) else it
    }
  }

  return urls.foldRight(null, ::fold)
}

fun ImageView.loadAvatars(urls: List<String>, fallback: Drawable? = null, crop: Boolean = true) {
  if (urls.isNotEmpty()) {
    GlideApp.with(this)
      .loadWithFallbacks(urls)
      ?.letIf(crop) {
        it.optionalCircleCrop()
      }
      ?.placeholder(fallback)
      ?.into(this)
  } else {
    GlideApp.with(this).clear(this)
    setImageDrawable(fallback)
  }
}
