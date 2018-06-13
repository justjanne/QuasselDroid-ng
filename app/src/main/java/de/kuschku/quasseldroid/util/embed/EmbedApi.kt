package de.kuschku.quasseldroid.util.embed

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface EmbedApi {
  @GET()
  fun embedData(@Query("url") url: String): Observable<EmbedResponse>
}
