package de.kuschku.quasseldroid.util.embed

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmbedHelper(baseUrl: String) {
  private val api = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .build()
    .create(EmbedApi::class.java)

  fun embedCode(url: String): Observable<EmbedResponse> = api.embedData(url)
}
