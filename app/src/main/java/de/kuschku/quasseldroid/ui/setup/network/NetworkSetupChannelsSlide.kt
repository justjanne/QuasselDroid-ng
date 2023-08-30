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

package de.kuschku.quasseldroid.ui.setup.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.setup.SlideFragment

class NetworkSetupChannelsSlide : SlideFragment() {
  lateinit var channelsWrapper: TextInputLayout
  lateinit var channelsField: EditText

  override fun isValid() = true

  override val title = R.string.slide_user_channels_title
  override val description = R.string.slide_user_channels_description

  override fun setData(data: Bundle) {
    if (data.containsKey("channels"))
      channelsField.setText(data.getStringArray("channels")?.joinToString("\n"))
    updateValidity()
  }

  override fun getData(data: Bundle) {
    data.putStringArray("channels",
                        channelsField.text.toString()
                          .split('\n', ' ', ',', ';')
                          .map(String::trim)
                          .filter(String::isNotBlank)
                          .toTypedArray()
    )
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_user_channels, container, false)
    this.channelsWrapper = view.findViewById(R.id.channelsWrapper)
    this.channelsField = view.findViewById(R.id.channels)
    return view
  }
}
