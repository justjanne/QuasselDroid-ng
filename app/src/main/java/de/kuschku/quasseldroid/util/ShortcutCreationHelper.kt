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

package de.kuschku.quasseldroid.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper.Companion.IGNORED_CHARS

object ShortcutCreationHelper {
  fun create(context: Context,
             messageSettings: MessageSettings,
             accountId: Long,
             info: BufferInfo,
             ircUser: IrcUser? = null) {
    val callback: (IconCompat) -> Unit = { icon ->
      ShortcutManagerCompat.requestPinShortcut(
        context,
        ShortcutInfoCompat.Builder(context, "${System.currentTimeMillis()}")
          .setShortLabel(info.bufferName ?: "")
          .setIcon(icon)
          .setIntent(
            ChatActivity.intent(
              context,
              bufferId = info.bufferId,
              accountId = accountId
            ).setAction(Intent.ACTION_VIEW)
          )
          .build(),
        null
      )
    }

    val resultAvailable: (Drawable) -> Unit = { resource ->
      val bitmap = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)
      resource.setBounds(0, 0, canvas.width, canvas.height)
      resource.draw(canvas)
      callback(IconCompat.createWithAdaptiveBitmap(bitmap))
    }

    val senderColors = context.theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(length()) {
        getColor(it, 0)
      }
    }

    val colorContext = ColorContext(context, messageSettings)

    if (info.type.hasFlag(Buffer_Type.QueryBuffer)) {
      val nickName = info.bufferName ?: ""
      val senderColorIndex = SenderColorUtil.senderColor(nickName)
      val rawInitial = nickName.trimStart(*IGNORED_CHARS).firstOrNull()
                       ?: nickName.firstOrNull()
      val initial = rawInitial?.toUpperCase().toString()
      val senderColor = senderColors[senderColorIndex]

      val fallback = colorContext.prepareTextDrawable()
        .beginConfig()
        .scale(0.5f)
        .endConfig()
        .buildRect(initial, senderColor)

      val urls = ircUser?.let {
        AvatarHelper.avatar(messageSettings, it, 432)
      }

      if (urls == null || urls.isEmpty()) {
        resultAvailable(fallback)
      } else {
        GlideApp.with(context)
          .loadWithFallbacks(urls)
          ?.placeholder(fallback)
          ?.into(object : SimpleTarget<Drawable>(432, 432) {
            override fun onResourceReady(resource: Drawable,
                                         transition: Transition<in Drawable>?) {
              resultAvailable(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
              resultAvailable(errorDrawable!!)
            }
          })
      }
    } else {
      callback(IconCompat.createWithResource(context, R.drawable.ic_shortcut_channel))
    }
  }
}
