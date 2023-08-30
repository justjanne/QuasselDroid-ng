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

package de.kuschku.quasseldroid.ui.clientsettings.crash

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import de.kuschku.malheur.CrashHandler
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.visibleIf
import kotlinx.serialization.json.Json
import java.io.File

class CrashFragment : DaggerFragment() {
  lateinit var list: RecyclerView
  lateinit var crashesEmpty: TextView

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  private var crashDir: File? = null
  private var adapter: CrashAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handlerThread = HandlerThread("Crash")
    handlerThread.start()
    handler = Handler(handlerThread.looper)
  }

  override fun onDestroy() {
    super.onDestroy()
    handlerThread.quit()
  }

  private fun reload() {
    val crashDir = this.crashDir
    val context = this.context

    if (crashDir != null && context != null) {
      crashDir.mkdirs()
      val list: List<Pair<Report?, Uri>> = crashDir.listFiles()
        .orEmpty()
        .map {
          Pair<Report?, Uri>(
            Json.decodeFromString<Report>(it.readText()),
            FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", it)
          )
        }
        .sortedByDescending { it.first?.environment?.crashTime }

      activity?.runOnUiThread {
        this.adapter?.submitList(list)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_crash, container, false)
    this.list = view.findViewById(R.id.list)
    this.crashesEmpty = view.findViewById(R.id.crashes_empty)

    setHasOptionsMenu(true)

    this.adapter = CrashAdapter()
    this.crashDir = File(requireContext().cacheDir, "crashes")

    list.layoutManager = LinearLayoutManager(context)
    list.adapter = adapter
    list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(list, false)

    adapter?.setOnUpdateListener {
      crashesEmpty.visibleIf(it.isEmpty())
    }

    handler.post(this::reload)
    return view
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.activity_crashes, menu)
    menu.findItem(R.id.action_generate_crash_report).isVisible = BuildConfig.DEBUG
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_generate_crash_report -> {
      handler.post {
        CrashHandler.handleSync(Exception("User requested generation of report"))
        reload()
      }
    }
    R.id.action_delete_all            -> {
      handler.post {
        crashDir?.mkdirs()
        crashDir?.listFiles()?.forEach {
          it.delete()
        }
        reload()
      }
      true
    }
    else                              -> super.onOptionsItemSelected(item)
  }
}
