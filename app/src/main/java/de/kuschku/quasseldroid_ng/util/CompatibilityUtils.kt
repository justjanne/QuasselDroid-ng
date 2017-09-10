package de.kuschku.quasseldroid_ng.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import java.io.OutputStream
import java.util.*
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

object CompatibilityUtils {
  /**
   * This method is used to check if the current device supports Sockets with the KeepAlive flag.
   *
   *
   * As that feature is only missing on Chromium devices, we just check for that
   *
   * @return Does the current device support KeepAlive sockets?
   */
  fun deviceSupportsKeepAlive(): Boolean {
    return !isChromeBook()
  }

  fun isChromeBook(): Boolean {
    return Build.MANUFACTURER.toLowerCase(Locale.ENGLISH).contains("chromium") ||
      Build.MANUFACTURER.toLowerCase(Locale.ENGLISH).contains("chrome") ||
      Build.BRAND.toLowerCase(Locale.ENGLISH).contains("chromium") ||
      Build.BRAND.toLowerCase(Locale.ENGLISH).contains("chrome")
  }

  /**
   * This method is used to check if the device supports SyncFlush
   * As that feature was only added in KitKat, we just check for the device version.
   *
   * @return Does the current device support SyncFlush natively?
   */
  fun deviceSupportsCompression(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
  }

  /**
   * Creates a SyncFlush output stream, even if the current device does not support doing so
   * natively.
   *
   * @param rawOut the raw output stream to be wrapped
   * @return The wrapping output stream
   */
  fun createDeflaterOutputStream(rawOut: OutputStream?): DeflaterOutputStream {
    return if (deviceSupportsCompression())
      DeflaterOutputStream(rawOut, true)
    else
      DeflaterOutputStream(rawOut, createSyncFlushDeflater())
  }

  /**
   * Creates a SyncFlush Deflater for use on pre-KitKat Android
   *
   * @return The modified Deflater, or null if the creation failed
   */
  private fun createSyncFlushDeflater(): Deflater? {
    val def = Deflater()
    try {
      val f = def.javaClass.getDeclaredField("flushParm")
      f.isAccessible = true
      f.setInt(def, 2) // Z_SYNC_FLUSH
    } catch (e: Exception) {
      return null
    }

    return def
  }
}

fun Context.getStatusBarHeight(): Int {
  var result = 0
  val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}

/**
 * Because Android’s String::split is broken
 *
 * @return A list with all substrings of length 1, in order
 */
fun String.split(): Array<String> {
  val chars = arrayOfNulls<String>(length)
  val charArray = toCharArray()
  return chars.indices.map { String(charArray, it, 1) }.toTypedArray()
}

/**
 * Modifies the display of an {@see Activity} in the Android Recents menu if the current version
 * of Android supports doing so.
 *
 * @param label The text shown as label - passed as Android String Resource
 * @param icon The icon displayed in recents - passed as Android Drawable Resource
 * @param colorPrimary The color used as background for the header of the recents card - passed as Android
 * Color Resource
 */
fun Activity.updateRecentsHeaderIfExisting(@StringRes label: Int, @DrawableRes icon: Int, @ColorRes colorPrimary: Int) {
  val labelRaw = resources.getString(label)
  val iconRaw = BitmapFactory.decodeResource(resources, icon)
  val colorPrimaryRaw = getColor(colorPrimary, theme, resources)
  updateRecentsHeaderIfExisting(labelRaw, iconRaw, colorPrimaryRaw)
}

@ColorInt
private fun getColor(@ColorRes color: Int, theme: Resources.Theme, resources: Resources): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    resources.getColor(color, theme)
  } else {
    // We have to use this method on older systems that don’t yet support the new method
    // which is used above
    resources.getColor(color)
  }
}

/**
 * Modifies the display of an {@see Activity} in the Android Recents menu if the current version
 * of Android supports doing so.
 *
 * @param label The text shown in recents as label
 * @param icon The icon displayed in recents
 * @param colorPrimary The color used as background for the header of the recents card
 */
fun Activity.updateRecentsHeaderIfExisting(label: String, icon: Bitmap, colorPrimary: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    updateRecentsHeader(label, icon, colorPrimary)
  }
}

/**
 * Forcibly updated the recents card of an {@see Activity} in the Android Recents menu.
 *
 * @param label The text shown in recents as label
 * @param icon The icon displayed in recents
 * @param colorPrimary The color used as background for the header of the recents card
 * @since Lollipop
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun Activity.updateRecentsHeader(label: String, icon: Bitmap,
                                         colorPrimary: Int) {
  setTaskDescription(ActivityManager.TaskDescription(label,
                                                     icon, colorPrimary))
}
