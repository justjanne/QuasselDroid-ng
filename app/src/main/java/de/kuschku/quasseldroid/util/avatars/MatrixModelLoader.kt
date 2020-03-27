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

package de.kuschku.quasseldroid.util.avatars

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import java.io.InputStream

class MatrixModelLoader(private val api: MatrixApi) :
  ModelLoader<Avatar.MatrixAvatar, InputStream> {
  override fun buildLoadData(model: Avatar.MatrixAvatar, width: Int, height: Int,
                             options: Options): ModelLoader.LoadData<InputStream>? {
    return ModelLoader.LoadData(ObjectKey(model), MatrixDataFetcher(model, api))
  }

  override fun handles(model: Avatar.MatrixAvatar): Boolean {
    return true
  }
}
