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

package de.kuschku.quasseldroid.util.avatars

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MatrixApi {
  @GET("/_matrix/client/r0/profile/{name}/avatar_url")
  fun avatarUrl(@Path("name") name: String): Call<MatrixAvatarResponse>

  @GET("/_matrix/media/r0/thumbnail/{server}/{id}/")
  fun avatarThumbnail(
    @Path("server") server: String,
    @Path("id") id: String,
    @Query("width") width: Int = 512,
    @Query("height") height: Int = 512,
    @Query("method") method: String = "scale"
  ): Call<ResponseBody>

  @GET("/_matrix/media/r0/download/{server}/{id}")
  fun avatarImage(
    @Path("server") server: String,
    @Path("id") id: String
  ): Call<ResponseBody>
}
