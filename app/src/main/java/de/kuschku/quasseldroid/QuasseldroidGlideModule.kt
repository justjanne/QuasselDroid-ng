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

package de.kuschku.quasseldroid

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import de.kuschku.quasseldroid.util.avatars.MatrixApi
import de.kuschku.quasseldroid.util.avatars.MatrixModelLoader
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import java.io.InputStream

@GlideModule
class QuasseldroidGlideModule : AppGlideModule() {
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    if (!BuildConfig.DEBUG) builder.setLogLevel(Log.ERROR)
  }

  override fun isManifestParsingEnabled() = false

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    val matrixApi = Retrofit.Builder()
      .baseUrl("https://matrix.org/")
      .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
      .build()
      .create(MatrixApi::class.java)

    registry.append(
      Avatar.MatrixAvatar::class.java,
      InputStream::class.java,
      object : ModelLoaderFactory<Avatar.MatrixAvatar, InputStream> {
        override fun build(
          multiFactory: MultiModelLoaderFactory
        ): ModelLoader<Avatar.MatrixAvatar, InputStream> {
          return MatrixModelLoader(matrixApi)
        }

        override fun teardown() = Unit
      })
  }
}
