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

package de.kuschku.quasseldroid.ui.info.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.attachment.AttachmentData
import de.kuschku.quasseldroid.util.ui.view.MessageAttachmentView

class MessageAttachmentAdapter(private val showLarge: Boolean) :
  ListAdapter<AttachmentData, MessageAttachmentAdapter.MessageAttachmentViewHolder>(
    object : DiffUtil.ItemCallback<AttachmentData>() {
      override fun areItemsTheSame(oldItem: AttachmentData, newItem: AttachmentData) =
        oldItem.fromUrl == newItem.fromUrl

      override fun areContentsTheSame(oldItem: AttachmentData, newItem: AttachmentData) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAttachmentViewHolder {
    return MessageAttachmentViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.widget_message_attachment_item, parent, false),
      showLarge
    )
  }

  override fun onBindViewHolder(holder: MessageAttachmentViewHolder, position: Int) {
    holder.bind(getItem(position))
  }


  class MessageAttachmentViewHolder(itemView: View, private val showLarge: Boolean) :
    RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.view)
    lateinit var attachmentView: MessageAttachmentView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(attachment: AttachmentData) {
      attachmentView.reinitViews()

      attachmentView.setLink(attachment.fromUrl)
      attachmentView.setColor(attachment.color)
      GlideApp.with(itemView)
        .load(attachment.authorIcon)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(attachmentView.authorIconTarget)
      attachmentView.setAuthor(attachment.authorName)
      //attachmentView.setAuthorLink(attachment.author_link)
      attachmentView.setTitle(attachment.title)
      attachmentView.setDescription(attachment.text)
      if (showLarge) {
        GlideApp.with(itemView)
          .clear(attachmentView.thumbnailTarget)
        GlideApp.with(itemView)
          .load(attachment.imageUrl)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(attachmentView.previewTarget)
      } else {
        GlideApp.with(itemView)
          .clear(attachmentView.previewTarget)
        GlideApp.with(itemView)
          .load(attachment.imageUrl)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(attachmentView.thumbnailTarget)
      }
      GlideApp.with(itemView)
        .load(attachment.serviceIcon)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(attachmentView.serviceIconTarget)
      attachmentView.setService(attachment.serviceName)
    }
  }
}
