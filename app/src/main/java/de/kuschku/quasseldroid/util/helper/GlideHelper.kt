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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.GlideRequest
import de.kuschku.quasseldroid.GlideRequests
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.viewmodel.data.Avatar

fun GlideRequests.loadWithFallbacks(urls: List<Avatar>): GlideRequest<Drawable>? {
  fun fold(url: Avatar, fallback: RequestBuilder<Drawable>?): GlideRequest<Drawable> {
    return when (url) {
      is Avatar.NativeAvatar   -> this.load(url.url)
      is Avatar.GravatarAvatar -> this.load(url.url)
      is Avatar.IRCCloudAvatar -> this.load(url.url)
      is Avatar.MatrixAvatar   -> this.load(url)
    }.let {
      if (fallback != null) it.error(fallback) else it
    }
  }

  return urls.foldRight(null, ::fold)
}

fun ImageView.loadAvatars(urls: List<Avatar>, fallback: Drawable? = null, crop: Boolean = true) {
  if (urls.isNotEmpty()) {
    GlideApp.with(this)
      .loadWithFallbacks(urls)
      ?.let {
        if (crop) {
          it.optionalCircleCrop()
        } else {
          it.transform(RoundedCorners(this.context.resources.getDimensionPixelSize(R.dimen.avatar_radius)))
        }
      }
      ?.placeholder(fallback)
      ?.into(this)
  } else {
    GlideApp.with(this).clear(this)
    setImageDrawable(fallback)
  }
}
