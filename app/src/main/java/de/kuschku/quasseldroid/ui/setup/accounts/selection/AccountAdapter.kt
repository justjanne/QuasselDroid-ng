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

package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.WidgetCoreAccountAddBinding
import de.kuschku.quasseldroid.databinding.WidgetCoreAccountBinding
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.util.lists.ListAdapter

class AccountAdapter : ListAdapter<Pair<Account?, Boolean>, AccountAdapter.AccountViewHolder>(
  object : DiffUtil.ItemCallback<Pair<Account?, Boolean>>() {
    override fun areItemsTheSame(oldItem: Pair<Account?, Boolean>,
                                 newItem: Pair<Account?, Boolean>) =
      oldItem.first?.id == newItem.first?.id

    override fun areContentsTheSame(oldItem: Pair<Account?, Boolean>,
                                    newItem: Pair<Account?, Boolean>) =
      oldItem == newItem
  }
) {
  private val actionListeners = mutableSetOf<(AccountId) -> Unit>()
  private val addListeners = mutableSetOf<() -> Unit>()
  private val clickListeners = mutableSetOf<(AccountId) -> Unit>()

  private val actionListener = object : ItemListener {
    override fun onAction(id: AccountId, pos: Int) {
      for (actionListener in actionListeners) {
        actionListener.invoke(id)
      }
    }
  }

  private val addListener = object : AddListener {
    override fun onAction() {
      for (addListener in addListeners) {
        addListener.invoke()
      }
    }
  }

  private val clickListener = object : ItemListener {
    override fun onAction(id: AccountId, pos: Int) {
      for (clickListener in clickListeners) {
        clickListener.invoke(id)
      }
    }
  }

  fun addEditListener(f: (AccountId) -> Unit) {
    actionListeners.add(f)
  }

  fun addAddListener(f: () -> Unit) {
    addListeners.add(f)
  }

  fun addClickListener(f: (AccountId) -> Unit) {
    clickListeners.add(f)
  }

  override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
    val (account, selected) = getItem(position)
    holder.bind(account, selected)
  }

  override fun getItemViewType(position: Int) = when (getItem(position).first) {
    null -> TYPE_ADD
    else -> TYPE_ACCOUNT
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    TYPE_ADD -> AccountViewHolder.Add(
      WidgetCoreAccountAddBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      addListener
    )
    else     -> AccountViewHolder.Item(
      WidgetCoreAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      actionListener,
      clickListener
    )
  }

  companion object {
    private const val TYPE_ACCOUNT = 0
    private const val TYPE_ADD = 1
  }

  interface ItemListener {
    fun onAction(id: AccountId, pos: Int)
  }

  interface AddListener {
    fun onAction()
  }

  sealed class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(account: Account?, selected: Boolean)

    class Item(
      private val binding: WidgetCoreAccountBinding,
      actionListener: ItemListener,
      clickListener: ItemListener
    ) : AccountViewHolder(binding.root) {
      private var data: Account? = null

      init {
        binding.accountEdit.setOnClickListener {
          actionListener.onAction(data?.id ?: AccountId(-1L), adapterPosition)
        }
        itemView.setOnClickListener {
          clickListener.onAction(data?.id ?: AccountId(-1L), adapterPosition)
        }
      }

      override fun bind(account: Account?, selected: Boolean) {
        data = account
        binding.accountName.text = account?.name
        binding.accountDescription.text = itemView.context.resources.getString(
          R.string.label_user_on_host, account?.user, account?.host, account?.port
        )
        binding.accountSelect.isChecked = selected
      }
    }

    class Add(
      private val binding: WidgetCoreAccountAddBinding,
      clickListener: AddListener
    ) : AccountViewHolder(binding.root) {
      init {
        itemView.setOnClickListener {
          clickListener.onAction()
        }
      }

      override fun bind(account: Account?, selected: Boolean) = Unit
    }
  }
}
