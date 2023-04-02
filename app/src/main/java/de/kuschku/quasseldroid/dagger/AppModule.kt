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

package de.kuschku.quasseldroid.dagger

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.Quasseldroid
import de.kuschku.quasseldroid.util.AndroidEmojiProvider
import de.kuschku.quasseldroid.util.avatars.MatrixApi
import de.kuschku.quasseldroid.util.emoji.EmojiProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
object AppModule {
  @Provides
  fun bindApplication(app: Quasseldroid): Application = app

  @Provides
  fun provideGson(): Gson = GsonBuilder().setPrettyPrinting().create()

  @Provides
  fun provideMatrixApi(): MatrixApi = Retrofit.Builder()
    .baseUrl("https://matrix.org/")
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .build()
    .create(MatrixApi::class.java)

  @Provides
  fun provideEmojiProvider(context: Application, gson: Gson): EmojiProvider =
    AndroidEmojiProvider(context.applicationContext, gson)
}
