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

package de.kuschku.quasseldroid.ui.setup.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.coresetup.CoreSetupBackend
import de.kuschku.libquassel.protocol.coresetup.CoreSetupData
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.setup.SlideFragment

abstract class CoreBackendChooseSlide : SlideFragment() {
  @BindView(R.id.account_list)
  lateinit var backendList: RecyclerView

  override fun isValid() = adapter.selection() != null

  protected val adapter = CoreBackendAdapter()

  abstract val outputKey: String
  abstract val inputKey: ((CoreSetupData) -> List<CoreSetupBackend>)

  override fun getData(data: Bundle) {
    data.putSerializable(
      outputKey,
      adapter.selection()
    )
  }

  override fun setData(data: Bundle) {
    val message = data.getSerializable("data") as? CoreSetupData
    adapter.submitList(message?.let(inputKey).orEmpty())
    (data.getSerializable(outputKey) as? CoreSetupBackend)?.let(adapter::updateSelection)
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_select_account, container, false)
    ButterKnife.bind(this, view)

    backendList.layoutManager = LinearLayoutManager(context)
    backendList.itemAnimator = DefaultItemAnimator()
    backendList.adapter = adapter
    adapter.addSelectionListener {
      updateValidity()
      hasChanged()
    }

    return view
  }
}
