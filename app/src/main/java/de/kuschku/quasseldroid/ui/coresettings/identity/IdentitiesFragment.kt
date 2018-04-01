package de.kuschku.quasseldroid.ui.coresettings.identity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

class IdentitiesFragment : ServiceBoundFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_list, container, false)
    ButterKnife.bind(this, view)

    val adapter = IdentityAdapter {
      val intent = Intent(requireContext(), IdentityActivity::class.java)
      intent.putExtra("identity", it)
      startActivity(intent)
    }

    list.adapter = adapter
    list.layoutManager = LinearLayoutManager(context)
    list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

    viewModel.identities.switchMap {
      combineLatest(it.values.map(Identity::liveUpdates)).map {
        it.map {
          IdentityItem(
            it.id(),
            it.identityName()
          )
        }
      }
    }.toLiveData().observe(this, Observer {
      adapter.submitList(it.orEmpty())
    })

    return view
  }

  data class IdentityItem(
    val id: IdentityId,
    val name: String
  )

  class IdentityAdapter(private val clickListener: (IdentityId) -> Unit) :
    ListAdapter<IdentityItem, IdentityAdapter.IdentityViewHolder>(
      object : DiffUtil.ItemCallback<IdentityItem>() {
        override fun areItemsTheSame(oldItem: IdentityItem, newItem: IdentityItem) =
          oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: IdentityItem, newItem: IdentityItem) =
          oldItem == newItem
      }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IdentityViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false),
      clickListener
    )

    override fun onBindViewHolder(holder: IdentityViewHolder, position: Int) {
      holder.bind(getItem(position))
    }

    class IdentityViewHolder(itemView: View, clickListener: (IdentityId) -> Unit) :
      RecyclerView.ViewHolder(itemView) {
      @BindView(R.id.title)
      lateinit var title: TextView

      var id: IdentityId? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          id?.let(clickListener::invoke)
        }
      }

      fun bind(item: IdentityItem) {
        this.id = item.id
        this.title.text = item.name
      }
    }
  }
}