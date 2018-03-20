package com.robobunny

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R

/*
 * Copyright (c) 2015 IRCCloud, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class SeekBarPreference : Preference, SeekBar.OnSeekBarChangeListener {

  private val TAG = javaClass.name

  private var maxValue = 100
  private var minValue = 0
  private var interval = 1
  private var currentValue: Int = 0

  private var unitsLeftText = ""
  private var unitsRightText = ""

  @BindView(R.id.seekBarPrefSeekBar)
  @JvmField
  var seekBar: AppCompatSeekBar? = null

  @BindView(R.id.seekBarPrefValue)
  @JvmField
  var statusText: TextView? = null

  @BindView(R.id.seekBarPrefUnitsLeft)
  @JvmField
  var unitsLeft: TextView? = null

  @BindView(R.id.seekBarPrefUnitsRight)
  @JvmField
  var unitsRight: TextView? = null

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {
    initPreference(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet) :
    super(context, attrs) {
    initPreference(context, attrs)
  }

  private fun initPreference(context: Context, attrs: AttributeSet) {
    setValuesFromXml(attrs)
    layoutResource = R.layout.preference_vertical
    widgetLayoutResource = R.layout.preference_seekbar
  }

  private fun setValuesFromXml(attrs: AttributeSet) {
    maxValue = attrs.getAttributeIntValue(NAMESPACE_ANDROID, "max", 100)
    minValue = attrs.getAttributeIntValue(NAMESPACE_ROBOBUNNY, "min", 0)
    unitsLeftText = getAttributeStringValue(attrs, NAMESPACE_ROBOBUNNY, "unitsLeft", "")
    val units = getAttributeStringValue(attrs, NAMESPACE_ROBOBUNNY, "units", "")
    unitsRightText = getAttributeStringValue(attrs, NAMESPACE_ROBOBUNNY, "unitsRight", units)
    try {
      val newInterval = attrs.getAttributeValue(NAMESPACE_ROBOBUNNY, "interval")
      if (newInterval != null)
        interval = Integer.parseInt(newInterval)
    } catch (e: Exception) {
      Log.e(TAG, "Invalid interval value", e)
    }
  }

  private fun getAttributeStringValue(attrs: AttributeSet, namespace: String, name: String,
                                      defaultValue: String) =
    attrs.getAttributeValue(namespace, name) ?: defaultValue

  override fun onBindViewHolder(holder: PreferenceViewHolder?) {
    super.onBindViewHolder(holder)
    holder?.itemView?.let { view ->
      ButterKnife.bind(this, view)
      seekBar?.max = maxValue - minValue
      seekBar?.setOnSeekBarChangeListener(this)
      statusText?.text = currentValue.toString()
      statusText?.minimumWidth = 30
      seekBar?.progress = currentValue - minValue
      unitsRight?.text = this.unitsRightText
      unitsLeft?.text = this.unitsLeftText
    }
  }

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    var newValue = progress + minValue
    if (newValue > maxValue)
      newValue = maxValue
    else if (newValue < minValue)
      newValue = minValue
    else if (interval != 1 && newValue % interval != 0)
      newValue = Math.round(newValue.toFloat() / interval) * interval
    // change rejected, revert to the previous value
    if (!callChangeListener(newValue)) {
      seekBar.progress = currentValue - minValue
      return
    }
    // change accepted, store it
    currentValue = newValue
    statusText?.text = newValue.toString()
    persistInt(newValue)
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
  override fun onStopTrackingTouch(seekBar: SeekBar) = notifyChanged()
  override fun onGetDefaultValue(ta: TypedArray, index: Int) = ta.getInt(index, DEFAULT_VALUE)
  override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
    if (restoreValue) {
      currentValue = getPersistedInt(currentValue)
    } else {
      var temp = 0
      try {
        temp = defaultValue as Int
      } catch (ex: Exception) {
        Log.e(TAG, "Invalid default value: " + defaultValue.toString())
      }
      persistInt(temp)
      currentValue = temp
    }
  }

  /**
   * make sure that the seekbar is disabled if the preference is disabled
   */
  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    seekBar?.isEnabled = enabled
  }

  override fun onDependencyChanged(dependency: Preference, disableDependent: Boolean) {
    super.onDependencyChanged(dependency, disableDependent)
    //Disable movement of seek bar when dependency is false
    seekBar?.isEnabled = !disableDependent
  }

  companion object {
    private const val NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"
    private const val NAMESPACE_ROBOBUNNY = "http://robobunny.com"
    private const val DEFAULT_VALUE = 50
  }
}