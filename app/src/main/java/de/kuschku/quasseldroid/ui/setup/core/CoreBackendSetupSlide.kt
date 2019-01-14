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

package de.kuschku.quasseldroid.ui.setup.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.coresetup.CoreSetupBackend
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.setup.SlideFragment

abstract class CoreBackendSetupSlide : SlideFragment() {
  @BindView(R.id.frame)
  lateinit var frame: LinearLayout

  @BindView(R.id.no_options_info)
  lateinit var noOptionsInfo: View

  override fun isValid() = true

  abstract val inputKey: String
  abstract val outputKey: String

  private var widgets = mutableListOf<QuasselSetupEntry>()

  override fun setData(data: Bundle) {
    widgets.clear()
    frame.removeAllViews()
    noOptionsInfo.visibility = View.VISIBLE

    val backend = data.getSerializable(inputKey) as? CoreSetupBackend
    for (configEntry in backend?.setupData.orEmpty()) {
      val entry = QuasselSetupEntry(frame.context,
                                    null,
                                    0,
                                    configEntry)
      widgets.add(entry)
      frame.addView(entry)
      noOptionsInfo.visibility = View.GONE
    }
  }

  override fun getData(data: Bundle) {
    val setupData = HashMap<String, QVariant_>()
    for (configEntry in widgets) {
      setupData[configEntry.key()] = configEntry.value()
    }
    data.putSerializable(outputKey, setupData)
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_core_backend_configure, container, false)
    ButterKnife.bind(this, view)

    return view
  }
}
