package de.kuschku.quasseldroid.util.helper

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Modifies the display of an {@see Activity} in the Android Recents menu if the current version
 * of Android supports doing so.
 *
 * @param label The text shown as label
 * @param icon The icon displayed in recents - passed as Android Drawable Resource
 * @param colorPrimary The color used as background for the header of the recents card - passed as Android
 * Color Resource
 */
fun Activity.updateRecentsHeaderIfExisting(
  label: String, @DrawableRes icon: Int, @ColorRes colorPrimary: Int) {
  val iconRaw = BitmapFactory.decodeResource(resources, icon)
  val colorPrimaryRaw = resources.getColorBackport(colorPrimary, theme)
  updateRecentsHeaderIfExisting(label, iconRaw, colorPrimaryRaw)
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
fun Activity.updateRecentsHeaderIfExisting(@StringRes label: Int, @DrawableRes icon: Int, @ColorRes
colorPrimary: Int) {
  val labelRaw = resources.getString(label)
  val iconRaw = BitmapFactory.decodeResource(resources, icon)
  val colorPrimaryRaw = resources.getColorBackport(colorPrimary, theme)
  updateRecentsHeaderIfExisting(labelRaw, iconRaw, colorPrimaryRaw)
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
  setTaskDescription(ActivityManager.TaskDescription(label, icon, colorPrimary))
}
