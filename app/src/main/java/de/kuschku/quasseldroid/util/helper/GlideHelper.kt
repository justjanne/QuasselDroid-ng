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
