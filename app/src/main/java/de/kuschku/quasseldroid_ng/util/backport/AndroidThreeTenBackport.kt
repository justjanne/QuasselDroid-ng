package de.kuschku.quasseldroid_ng.util.backport

import android.content.Context
import org.threeten.bp.zone.TzdbZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesProvider
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean


object AndroidThreeTenBackport {
  private val initialized = AtomicBoolean()

  fun init(context: Context) {
    if (initialized.getAndSet(true)) {
      return
    }

    val provider: TzdbZoneRulesProvider
    var inputStream: InputStream? = null
    try {
      inputStream = context.assets.open("org/threeten/bp/TZDB.dat")
      provider = TzdbZoneRulesProvider(inputStream)
    } catch (e: IOException) {
      throw IllegalStateException("TZDB.dat missing from assets.", e)
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close()
        } catch (ignored: IOException) {
        }

      }
    }

    ZoneRulesProvider.registerProvider(provider)
  }
}