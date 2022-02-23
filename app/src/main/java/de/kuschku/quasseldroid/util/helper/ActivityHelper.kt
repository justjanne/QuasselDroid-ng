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

package de.kuschku.quasseldroid.util.helper

import android.app.Activity
import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Modifies the display of an {@see Activity} in the Android Recents menu if the current version
 * of Android supports doing so.
 *
 * @param label The text shown as label
 * @param icon The icon displayed in recents - passed as Android Drawable Resource
 * @param colorPrimary The color used as background for the header of the recents card - passed as Android
 * Color Resource
 */
fun Activity.updateRecentsHeader(
  label: String,
  @DrawableRes icon: Int,
  @ColorRes colorPrimary: Int
) {
  val colorPrimaryRaw = resources.getColorBackport(colorPrimary, theme)
  updateRecentsHeaderInternal(label, icon, colorPrimaryRaw)
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
fun Activity.updateRecentsHeader(
  @StringRes label: Int,
  @DrawableRes icon: Int,
  @ColorRes colorPrimary: Int
) {
  val labelRaw = resources.getString(label)
  val colorPrimaryRaw = resources.getColorBackport(colorPrimary, theme)
  updateRecentsHeaderInternal(labelRaw, icon, colorPrimaryRaw)
}

/**
 * Forcibly updated the recents card of an {@see Activity} in the Android Recents menu.
 *
 * @param label The text shown in recents as label
 * @param icon The icon displayed in recents
 * @param colorPrimary The color used as background for the header of the recents card
 */
private fun Activity.updateRecentsHeaderInternal(
  label: String,
  @DrawableRes icon: Int,
  @ColorInt colorPrimary: Int
) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    setTaskDescription(ActivityManager.TaskDescription(label, icon, colorPrimary))
  } else {
    val iconRaw = BitmapFactory.decodeResource(resources, icon)
    @Suppress("DEPRECATION")
    setTaskDescription(ActivityManager.TaskDescription(label, iconRaw, colorPrimary))
  }
}
