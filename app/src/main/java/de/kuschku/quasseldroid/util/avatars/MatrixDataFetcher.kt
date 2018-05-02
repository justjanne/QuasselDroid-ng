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

package de.kuschku.quasseldroid.util.avatars

import android.net.Uri
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import okhttp3.Call
import java.io.InputStream

class MatrixDataFetcher(
  private val model: Avatar.MatrixAvatar,
  private val api: MatrixApi
) :
  DataFetcher<InputStream> {
  private var call: Call? = null

  override fun getDataClass(): Class<InputStream> {
    return InputStream::class.java
  }

  override fun cleanup() {
  }

  override fun getDataSource(): DataSource {
    return DataSource.REMOTE
  }

  override fun cancel() {
    call?.cancel()
  }

  private fun loadAvatarInfo(model: Avatar.MatrixAvatar) =
    api.avatarUrl(model.userId).execute().body()?.let {
      it.avatarUrl?.let {
        MatrixAvatarInfo(it, model.size)
      }
    }

  private fun inputStreamFromAvatarInfo(info: MatrixAvatarInfo): InputStream? {
    val url = Uri.parse(info.avatarUrl)
    return if (info.size != null && info.size < 512) {
      api.avatarThumbnail(server = url.host,
                          id = url.pathSegments.first(),
                          width = info.size,
                          height = info.size,
                          method = if (info.size > 96) "scale" else "crop")
    } else {
      api.avatarImage(server = url.host, id = url.pathSegments.first())
    }.execute().body()?.byteStream()
  }

  override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
    loadAvatarInfo(model)?.let {
      inputStreamFromAvatarInfo(it)
    }?.let {
      callback.onDataReady(it)
    } ?: callback.onLoadFailed(IllegalStateException("Unknown Error!"))
  }
}
