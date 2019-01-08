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

package de.kuschku.quasseldroid.ui.clientsettings.crash

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.fromJson
import java.io.File
import javax.inject.Inject

class CrashFragment : DaggerFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  @Inject
  lateinit var gson: Gson

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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_crash, container, false)
    ButterKnife.bind(this, view)

    setHasOptionsMenu(true)

    this.adapter = CrashAdapter()
    this.crashDir = File(requireContext().cacheDir, "crashes")

    list.layoutManager = LinearLayoutManager(context)
    list.adapter = adapter
    list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(list, false)

    handler.post {
      val crashDir = this.crashDir
      val gson = this.gson
      val context = this.context

      if (crashDir != null && context != null) {
        crashDir.mkdirs()
        val list: List<Pair<Report, Uri>> = crashDir.listFiles()
          .orEmpty()
          .map {
            Pair<Report, Uri>(
              gson.fromJson(it.readText()),
              FileProvider.getUriForFile(context, "de.kuschku.quasseldroid.fileprovider", it)
            )
          }
          .sortedByDescending { it.first.environment?.crashTime }

        activity?.runOnUiThread {
          this.adapter?.submitList(list)
        }
      }
    }
    return view
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.activity_crashes, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_delete_all -> {
      handler.post {
        crashDir?.mkdirs()
        crashDir?.listFiles()?.forEach {
          it.delete()
        }
        activity?.runOnUiThread {
          this.adapter?.submitList(emptyList())
        }
      }
      true
    }
    else                   -> super.onOptionsItemSelected(item)
  }
}
