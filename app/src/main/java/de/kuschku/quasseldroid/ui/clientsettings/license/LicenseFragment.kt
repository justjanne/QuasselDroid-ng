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

package de.kuschku.quasseldroid.ui.clientsettings.license

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.R

class LicenseFragment : DaggerFragment() {
  lateinit var text: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_license, container, false)
    this.text = view.findViewById(R.id.text)

    val textResource = arguments?.getInt("license_text", 0) ?: 0
    if (textResource != 0) {
      text.text = Html.fromHtml(resources.openRawResource(textResource).bufferedReader(Charsets.UTF_8).readText())
    }

    return view
  }
}
