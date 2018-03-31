package de.kuschku.quasseldroid.ui.clientsettings.about

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

class ContributorAdapter(private val contributors: List<Contributor>) :
  RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContributorViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_contributor, parent, false)
  )

  override fun getItemCount() = contributors.size

  override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
    holder.bind(contributors[position])
  }

  class ContributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.nickname)
    lateinit var nickName: TextView

    @BindView(R.id.description)
    lateinit var description: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(item: Contributor) {
      this.name.text = item.name
      this.nickName.text = item.nickName
      this.description.text = item.description
    }
  }
}