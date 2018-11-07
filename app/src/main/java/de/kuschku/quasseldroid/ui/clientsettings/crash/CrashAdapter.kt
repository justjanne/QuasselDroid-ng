/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.clientsettings.crash

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.R
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class CrashAdapter : ListAdapter<Pair<Report, String>, CrashAdapter.CrashViewHolder>(
  object : DiffUtil.ItemCallback<Pair<Report, String>>() {
    override fun areItemsTheSame(oldItem: Pair<Report, String>, newItem: Pair<Report, String>) =
      oldItem.second == newItem.second

    override fun areContentsTheSame(oldItem: Pair<Report, String>, newItem: Pair<Report, String>) =
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
