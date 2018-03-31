package de.kuschku.quasseldroid.ui.coresettings

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigActivity
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

class CoreSettingsFragment : ServiceBoundFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_coresettings, container, false)
    ButterKnife.bind(this, view)

    list.layoutManager = LinearLayoutManager(context)
    list.adapter = CoreSettingsAdapter(listOf(
      CoreSetting(
        getString(R.string.settings_networkconfig_title),
        getString(R.string.settings_networkconfig_description),
        Intent(requireContext(), NetworkConfigActivity::class.java)
      )
    ))

    return view
  }

  data class CoreSetting(
    val title: CharSequence,
    val summary: CharSequence? = null,
    val intent: Intent? = null
  )

  class CoreSettingsAdapter(private val data: List<CoreSetting>) :
    RecyclerView.Adapter<CoreSettingsAdapter.CoreSettingsViewHolder>() {
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CoreSettingsViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.widget_coresetting, parent, false)
    )

    override fun onBindViewHolder(holder: CoreSettingsViewHolder, position: Int) {
      holder.bind(data[position])
    }

    class CoreSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      @BindView(R.id.title)
      lateinit var title: TextView

      @BindView(R.id.summary)
      lateinit var summary: TextView

      var item: CoreSetting? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          item?.intent?.let(itemView.context::startActivity)
        }
      }

      fun bind(item: CoreSetting) {
        this.item = item

        this.title.text = item.title
        this.summary.text = item.summary
        this.summary.visibleIf(!item.summary.isNullOrBlank())
      }
    }
  }
}