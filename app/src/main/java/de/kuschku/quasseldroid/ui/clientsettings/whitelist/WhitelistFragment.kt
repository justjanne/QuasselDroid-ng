/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.SettingsFragment
import javax.inject.Inject

class WhitelistFragment : SettingsFragment(), Changeable,
                          Savable {
  @BindView(R.id.certificate_whitelist)
  lateinit var certificateList: RecyclerView

  @BindView(R.id.certificate_whitelist_empty)
  lateinit var certificateListEmpty: TextView

  @BindView(R.id.hostname_whitelist)
  lateinit var hostnameList: RecyclerView

  @BindView(R.id.hostname_whitelist_empty)
  lateinit var hostnameListEmpty: TextView

  @Inject
  lateinit var database: QuasselDatabase

  private var whitelist: Whitelist? = null

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  private var certificateAdapter = WhitelistCertificateAdapter()
  private var hostnameAdapter = WhitelistHostnameAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handlerThread = HandlerThread("Whitelist")
    handlerThread.start()
    handler = Handler(handlerThread.looper)
  }

  override fun onDestroy() {
    super.onDestroy()
    handlerThread.quit()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_whitelist, container, false)
    ButterKnife.bind(this, view)

    setHasOptionsMenu(true)

    certificateList.layoutManager = LinearLayoutManager(context)
    certificateList.adapter = certificateAdapter
    certificateList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(certificateList, false)

    hostnameList.layoutManager = LinearLayoutManager(context)
    hostnameList.adapter = hostnameAdapter
    hostnameList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(hostnameList, false)

    certificateAdapter.setOnUpdateListener {
      activity?.runOnUiThread {
        certificateListEmpty.visibleIf(it.isNullOrEmpty())
      }
    }

    hostnameAdapter.setOnUpdateListener {
      activity?.runOnUiThread {
        hostnameListEmpty.visibleIf(it.isNullOrEmpty())
      }
    }

    handler.post {
      whitelist = Whitelist(database.validityWhitelist().all(), database.hostnameWhitelist().all())
      whitelist?.let {
        certificateAdapter.list = it.certificates
        hostnameAdapter.list = it.hostnames
      }
    }
    return view
  }

  fun applyChanges() = Whitelist(
    certificateAdapter.list,
    hostnameAdapter.list
  )

  override fun onSave() = whitelist?.let {
    val data = applyChanges()
    handler.post {
      database.runInTransaction {
        for (item in it.certificates - data.certificates) {
          database.validityWhitelist().delete(item.fingerprint)
        }
        for (item in it.hostnames - data.hostnames) {
          database.hostnameWhitelist().delete(item.fingerprint, item.hostname)
        }
      }
    }
    true
  } ?: false

  override fun hasChanged() = whitelist != applyChanges()
}
