package de.kuschku.quasseldroid.ui.settings.crash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.view.ViewCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.fromJson
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class CrashSettingsFragment : ServiceBoundFragment() {
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
        val list: List<Pair<Report, String>> = crashDir.listFiles()
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

  class CrashAdapter : ListAdapter<Pair<Report, String>, CrashAdapter.CrashViewHolder>(
    object : DiffUtil.ItemCallback<Pair<Report, String>>() {
      override fun areItemsTheSame(oldItem: Pair<Report, String>?, newItem: Pair<Report, String>?) =
        oldItem?.second == newItem?.second

      override fun areContentsTheSame(oldItem: Pair<Report, String>?,
                                      newItem: Pair<Report, String>?) =
        oldItem == newItem
    }
  ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CrashViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.widget_crash, parent, false)
    )

    override fun onBindViewHolder(holder: CrashViewHolder, position: Int) {
      val (report, data) = getItem(position)
      holder.bind(report, data)
    }

    class CrashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      @BindView(R.id.crash_time)
      lateinit var crashTime: TextView

      @BindView(R.id.version_name)
      lateinit var versionName: TextView

      @BindView(R.id.error)
      lateinit var error: TextView

      var item: Report? = null
      var data: String? = null

      private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          data?.let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/json"
            intent.putExtra(Intent.EXTRA_TEXT, it)
            itemView.context.startActivity(
              Intent.createChooser(
                intent,
                itemView.context.getString(R.string.label_share_crashreport)
              )
            )
          }
        }
      }

      fun bind(item: Report, data: String) {
        this.item = item
        this.data = data

        this.crashTime.text = item.environment?.crashTime?.let {
          dateTimeFormatter.format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()))
        }
        this.versionName.text = "${item.application?.versionName}"
        this.error.text = "${item.crash?.exception?.lines()?.firstOrNull()}"
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.activity_crashes, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_delete_all -> {
      runInBackground {
        File(requireContext().cacheDir, "crashes").listFiles().forEach {
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
