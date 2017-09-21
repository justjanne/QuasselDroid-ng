package de.kuschku.quasseldroid_ng.util

import android.os.Build
import de.kuschku.libquassel.util.CompatibilityUtils
import java.util.*

object AndroidCompatibilityUtils {
  fun init() {
    /**
     * This is used to check if the current device supports Sockets with the KeepAlive flag.
     * As that feature is only missing on Chromium devices, we just check for that
     *
     * @return Does the current device support KeepAlive sockets?
     */
    CompatibilityUtils.supportsKeepAlive = !isChromeBook()

    /**
     * This is used to check if the device supports SyncFlush
     * As that feature was only added in KitKat, we just check for the device version.
     */
    CompatibilityUtils.supportsCompression = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
  }

  private fun isChromeBook(): Boolean {
    return Build.MANUFACTURER.toLowerCase(Locale.ENGLISH).contains("chromium") ||
      Build.MANUFACTURER.toLowerCase(Locale.ENGLISH).contains("chrome") ||
      Build.BRAND.toLowerCase(Locale.ENGLISH).contains("chromium") ||
      Build.BRAND.toLowerCase(Locale.ENGLISH).contains("chrome")
  }
}
