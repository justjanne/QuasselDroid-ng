/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.preference.DialogPreference
import android.text.TextUtils
import android.util.AttributeSet
import de.kuschku.quasseldroid.R

open class RingtonePreference : DialogPreference,
                                RequiresActivityLauncher,
                                OnActivityResultListener {
  private val TAG = "RingtonePreference"

  private var mRingtoneType: Int = 0
  private var mShowDefault: Boolean = false
  private var mShowSilent: Boolean = false

  private var mRequestCode: Int? = null

  override var activityLauncher: ActivityLauncher? = null
    set(value) {
      field?.unregisterOnActivityResultListener(this)

      field = value

      value?.registerOnActivityResultListener(this)
      mRequestCode = value?.getNextRequestCode()
    }

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, R.attr.ringtonePreferenceStyle)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    this(context, attrs, defStyleAttr, 0)

  constructor(context: Context, attrs: AttributeSet?, styleAttr: Int, styleRes: Int) :
    super(context, attrs, styleAttr, styleRes) {
    val a = context.obtainStyledAttributes(
      attrs, R.styleable.RingtonePreference, styleAttr, styleRes)
    mRingtoneType = a.getInt(R.styleable.RingtonePreference_ringtoneType,
                             RingtoneManager.TYPE_RINGTONE)
    mShowDefault = a.getBoolean(R.styleable.RingtonePreference_showDefault, true)
    mShowSilent = a.getBoolean(R.styleable.RingtonePreference_showSilent, true)
    a.recycle()
  }

  /**
   * Returns the sound type(s) that are shown in the picker.
   *
   * @return The sound type(s) that are shown in the picker.
   * @see .setRingtoneType
   */
  fun getRingtoneType(): Int {
    return mRingtoneType
  }

  /**
   * Sets the sound type(s) that are shown in the picker.
   *
   * @param type The sound type(s) that are shown in the picker.
   * @see RingtoneManager.EXTRA_RINGTONE_TYPE
   */
  fun setRingtoneType(type: Int) {
    mRingtoneType = type
  }

  /**
   * Returns whether to a show an item for the default sound/ringtone.
   *
   * @return Whether to show an item for the default sound/ringtone.
   */
  fun getShowDefault(): Boolean {
    return mShowDefault
  }

  /**
   * Sets whether to show an item for the default sound/ringtone. The default
   * to use will be deduced from the sound type(s) being shown.
   *
   * @param showDefault Whether to show the default or not.
   * @see RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT
   */
  fun setShowDefault(showDefault: Boolean) {
    mShowDefault = showDefault
  }

  /**
   * Returns whether to a show an item for 'Silent'.
   *
   * @return Whether to show an item for 'Silent'.
   */
  fun getShowSilent(): Boolean {
    return mShowSilent
  }

  /**
   * Sets whether to show an item for 'Silent'.
   *
   * @param showSilent Whether to show 'Silent'.
   * @see RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT
   */
  fun setShowSilent(showSilent: Boolean) {
    mShowSilent = showSilent
  }

  override fun onClick() {
    // Launch the ringtone picker
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
    onPrepareRingtonePickerIntent(intent)
    mRequestCode?.let {
      activityLauncher?.startActivityForResult(intent, it)
    }
  }

  /**
   * Prepares the intent to launch the ringtone picker. This can be modified
   * to adjust the parameters of the ringtone picker.
   *
   * @param ringtonePickerIntent The ringtone picker intent that can be
   * modified by putting extras.
   */
  protected fun onPrepareRingtonePickerIntent(ringtonePickerIntent: Intent) {

    ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, mShowDefault)
    ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, mShowSilent)
    ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, mRingtoneType)
    ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, title)
    if (mShowDefault) {
      ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                                    RingtoneManager.getDefaultUri(getRingtoneType()))
    }
    ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, onRestoreRingtone())
  }

  /**
   * Called when a ringtone is chosen.
   *
   *
   * By default, this saves the ringtone URI to the persistent storage as a
   * string.
   *
   * @param ringtoneUri The chosen ringtone's [Uri]. Can be null.
   */
  protected fun onSaveRingtone(ringtoneUri: Uri?) {
    persistString(ringtoneUri?.toString() ?: "")
    updateSummary(ringtoneUri)
  }

  /**
   * Called when the chooser is about to be shown and the current ringtone
   * should be marked. Can return null to not mark any ringtone.
   *
   *
   * By default, this restores the previous ringtone URI from the persistent
   * storage.
   *
   * @return The ringtone to be marked as the current ringtone.
   */
  protected fun onRestoreRingtone(): Uri? {
    val uriString = getPersistedString(null)
    return if (!TextUtils.isEmpty(uriString)) Uri.parse(uriString) else null
  }

  private fun updateSummary(ringtoneUri: Uri?) {
    summary = ringtoneUri?.let {
      RingtoneManager.getRingtone(context, ringtoneUri)?.getTitle(context)
    } ?: context.getString(R.string.label_no_sound)
  }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
    return a.getString(index)
  }

  override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValueObj: Any?) {
    val defaultValue = defaultValueObj as? String ?: ""

    if (restorePersistedValue) {
      updateSummary(onRestoreRingtone())
      return
    }

    // If we are setting to the default value, we should persist it.
    if (!TextUtils.isEmpty(defaultValue)) {
      onSaveRingtone(Uri.parse(defaultValue))
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == mRequestCode) {
      if (data != null) {
        val uri = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        if (callChangeListener(uri?.toString() ?: "")) {
          onSaveRingtone(uri)
        }
      }
      return true
    }
    return false
  }
}
