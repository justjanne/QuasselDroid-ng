package de.kuschku.quasseldroid.ui.clientsettings.about

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseSettingsActivity
import de.kuschku.quasseldroid.util.helper.visibleIf

class LibraryAdapter(private val libraries: List<Library>) :
  RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LibraryViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_library, parent, false)
  )

  override fun getItemCount() = libraries.size

  override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
    holder.bind(libraries[position])
  }

  class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.version)
    lateinit var version: TextView

    @BindView(R.id.license)
    lateinit var license: TextView

    private var item: Library? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        this.item?.run {
          val intent = Intent(itemView.context, LicenseSettingsActivity::class.java)
          intent.putExtra("license_name", license.fullName)
          intent.putExtra("license_text", license.text)
          itemView.context.startActivity(intent)
        }
      }
    }

    fun bind(item: Library) {
      this.item = item
      this.name.text = item.name
      this.version.text = item.version
      this.version.visibleIf(!item.version.isNullOrBlank())
      this.license.text = item.license.shortName
    }
  }
}