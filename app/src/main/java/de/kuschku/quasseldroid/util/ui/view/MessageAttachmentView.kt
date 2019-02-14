/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.util.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.visibleIf

class MessageAttachmentView : FrameLayout {
  @BindView(R.id.attachment_color_bar)
  lateinit var colorBar: View

  @BindView(R.id.attachment_author_icon)
  lateinit var authorIcon: ImageView

  @BindView(R.id.attachment_author)
  lateinit var author: TextView

  @BindView(R.id.attachment_title)
  lateinit var title: TextView

  @BindView(R.id.attachment_description)
  lateinit var description: TextView

  @BindView(R.id.attachment_thumbnail)
  lateinit var thumbnail: ImageView

  @BindView(R.id.attachment_preview)
  lateinit var preview: ImageView

  @BindView(R.id.attachment_service_icon)
  lateinit var serviceIcon: ImageView

  @BindView(R.id.attachment_service)
  lateinit var service: TextView

  val authorIconTarget: VisibilitySettingDrawableImageViewTarget
  val thumbnailTarget: VisibilitySettingDrawableImageViewTarget
  val previewTarget: VisibilitySettingDrawableImageViewTarget
  val serviceIconTarget: VisibilitySettingDrawableImageViewTarget

  private var url: String? = null
  private var authorUrl: String? = null

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    LayoutInflater.from(context).inflate(R.layout.widget_message_attachment, this, true)
    ButterKnife.bind(this)

    authorIconTarget = VisibilitySettingDrawableImageViewTarget(authorIcon)
    thumbnailTarget = VisibilitySettingDrawableImageViewTarget(thumbnail)
    previewTarget = VisibilitySettingDrawableImageViewTarget(preview)
    serviceIconTarget = VisibilitySettingDrawableImageViewTarget(serviceIcon)

    reinitViews()
  }

  fun reinitViews() {
    setColor(0)
    setAuthorIcon(null)
    authorIcon.visibility = View.VISIBLE
    setAuthor("")
    setTitle("")
    setDescription("")
    setThumbnail(null)
    thumbnail.visibility = View.VISIBLE
    setPreview(null)
    preview.visibility = View.VISIBLE
    setServiceIcon(null)
    serviceIcon.visibility = View.VISIBLE
    setService("")
  }

  class VisibilitySettingDrawableImageViewTarget(view: ImageView) :
    CustomViewTarget<ImageView, Drawable>(view) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
      view.setImageDrawable(errorDrawable)
      view.visibility = View.GONE
    }

    override fun onResourceCleared(placeholder: Drawable?) {
      view.setImageDrawable(placeholder)
      view.visibility = View.GONE
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
      view.setImageDrawable(resource)
      view.visibility = View.VISIBLE
    }
  }

  private val colorForegroundSecondary = context.theme.styledAttributes(R.attr.colorForegroundSecondary) {
    getColor(0, 0)
  }

  fun setColor(color: String?) {
    setColor(try {
      Color.parseColor(color)
    } catch (ignored: Throwable) {
      0
    })
  }

  fun setColor(@ColorInt color: Int) {
    if (color != 0) {
      colorBar.setBackgroundColor(color)
    } else {
      colorBar.setBackgroundColor(colorForegroundSecondary)
    }
  }

  fun setAuthorIcon(drawable: Drawable?) {
    authorIcon.setImageDrawable(drawable)
  }

  fun setAuthor(text: String?) {
    author.text = text
    author.visibleIf(!text.isNullOrBlank())
  }

  fun setAuthorLink(url: String) {
    if (url.isNotBlank()) {
      author.setOnClickListener {
        context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse(url)
        })
      }
    } else {
      author.setOnClickListener(null)
    }
  }

  fun setTitle(text: String?) {
    title.text = text
    title.visibleIf(!text.isNullOrBlank())
  }

  fun setLink(url: String?) {
    if (url.isNullOrBlank()) {
      this.setOnClickListener(null)
    } else {
      this.setOnClickListener {
        context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse(url)
        })
      }
    }
  }

  fun setDescription(text: String?) {
    description.text = text
    description.visibleIf(!text.isNullOrBlank())
  }

  fun setThumbnail(drawable: Drawable?) {
    thumbnail.setImageDrawable(drawable)
  }

  fun setPreview(drawable: Drawable?) {
    preview.setImageDrawable(drawable)
  }

  fun setServiceIcon(drawable: Drawable?) {
    serviceIcon.setImageDrawable(drawable)
  }

  fun setService(text: String?) {
    service.text = text
    service.visibleIf(!text.isNullOrBlank())
  }
}
