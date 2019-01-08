/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.util.compatibility

import android.os.Build
import de.kuschku.libquassel.util.compatibility.CompatibilityUtils
import java.util.*

object AndroidCompatibilityUtils {
  fun inject() {
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
