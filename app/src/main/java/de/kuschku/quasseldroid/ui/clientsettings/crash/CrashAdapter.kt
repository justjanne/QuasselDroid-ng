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

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.malheur.data.Report
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.WidgetCrashBinding
import de.kuschku.quasseldroid.util.lists.ListAdapter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class CrashAdapter : ListAdapter<Pair<Report?, Uri>, CrashAdapter.CrashViewHolder>(
  object : DiffUtil.ItemCallback<Pair<Report?, Uri>>() {
    override fun areItemsTheSame(oldItem: Pair<Report?, Uri>, newItem: Pair<Report?, Uri>) =
      oldItem.second == newItem.second

    override fun areContentsTheSame(oldItem: Pair<Report?, Uri>, newItem: Pair<Report?, Uri>) =
      oldItem == newItem
  }
) {
  private var onUpdateListener: ((List<Pair<Report?, Uri>>) -> Unit)? = null
  fun setOnUpdateListener(listener: ((List<Pair<Report?, Uri>>) -> Unit)?) {
    onUpdateListener = listener
  }

  override fun onUpdateFinished(list: List<Pair<Report?, Uri>>) {
    onUpdateListener?.invoke(list)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CrashViewHolder(
    WidgetCrashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  )

  override fun onBindViewHolder(holder: CrashViewHolder, position: Int) {
    val (report, uri) = getItem(position)
    holder.bind(report, uri)
  }

  class CrashViewHolder(
    private val binding: WidgetCrashBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    private var item: Report? = null
    private var uri: Uri? = null

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")

    init {
      itemView.setOnClickListener {
        uri?.let {
          itemView.context.startActivity(
            Intent.createChooser(
              Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
              },
              itemView.context.getString(R.string.label_share_crashreport)
            )
          )
        }
      }
    }

    fun bind(item: Report?, uri: Uri) {
      this.item = item
      this.uri = uri

      binding.crashTime.text = item?.timestamp?.let {
        dateTimeFormatter.format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()))
      } ?: "null"
      binding.versionName.text = item?.application?.versionName ?: "null"
      binding.error.text = item?.crash?.exception?.lines()?.firstOrNull() ?: "null"
    }
  }
}
