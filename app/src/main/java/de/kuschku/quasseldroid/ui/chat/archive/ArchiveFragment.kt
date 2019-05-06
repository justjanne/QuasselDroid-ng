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

package de.kuschku.quasseldroid.ui.chat.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.helper.QuasselViewModelHelper
import javax.inject.Inject

class ArchiveFragment : ServiceBoundFragment() {
  @BindView(R.id.list_temporary)
  lateinit var listTemporary: RecyclerView

  @BindView(R.id.list_permanently)
  lateinit var listPermanently: RecyclerView

  @Inject
  lateinit var modelHelper: QuasselViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.chat_archive, container, false)
    ButterKnife.bind(this, view)

    val chatlistId = arguments?.getInt("chatlist_id", -1)

    val chatlist = modelHelper.bufferViewConfigMap.map {
      it[chatlistId]
    }

    return view
  }
}
