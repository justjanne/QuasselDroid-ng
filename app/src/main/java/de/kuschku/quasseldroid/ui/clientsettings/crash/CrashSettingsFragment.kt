package de.kuschku.quasseldroid.ui.clientsettings.crash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.android.support.DaggerFragment
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.fromJson
import java.io.File

class CrashSettingsFragment : DaggerFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  private var gson: Gson? = null
  private var crashDir: File? = null
  private var adapter: CrashAdapter? = null

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    handlerThread = HandlerThread("CrashSettings")
    handlerThread.start()
    handler = Handler(handlerThread.looper)
  }

  override fun onDetach() {
    super.onDetach()
    handlerThread.quit()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_crash, container, false)
    ButterKnife.bind(this, view)

    setHasOptionsMenu(true)

    this.adapter = CrashAdapter()
    this.crashDir = File(requireContext().cacheDir, "crashes")
    this.gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

    list.layoutManager = LinearLayoutManager(context)
    list.adapter = adapter
    list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(list, false)

    handler.post {
      val crashDir = this.crashDir
      val gson = this.gson

      if (crashDir != null && gson != null) {
        crashDir.mkdirs()
        val list: List<Pair<Report, String>> = crashDir.listFiles()
          .orEmpty()
          .map { it.readText() }
          .map { Pair<Report, String>(gson.fromJson(it), it) }
          .sortedByDescending { it.first.environment?.crashTime }

        requireActivity().runOnUiThread {
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
        requireActivity().runOnUiThread {
          this.adapter?.submitList(emptyList())
        }
      }
      true
    }
    else                   -> super.onOptionsItemSelected(item)
  }
}
