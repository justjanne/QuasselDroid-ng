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

package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.util.helper.zip

class AccountAdapter(
  owner: LifecycleOwner,
  liveData: LiveData<List<Account>>,
  private val selectedItem: MutableLiveData<Pair<Long, Long>>
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {
  private val actionListeners = mutableSetOf<(Long) -> Unit>()
  private val addListeners = mutableSetOf<() -> Unit>()
  private val selectionListeners = mutableSetOf<(Long) -> Unit>()

  private val clickListener = object :
    ItemListener {
    override fun onAction(id: Long, pos: Int) {
      selectionListener.invoke(id)
    }
  }

  private val actionListener = object :
    ItemListener {
    override fun onAction(id: Long, pos: Int) {
      for (actionListener in actionListeners) {
        actionListener.invoke(id)
      }
    }
  }

  private fun updateSelection(id: Long) {
    selectedItem.value = Pair(selectedItem.value?.second ?: -1, id)
  }

  private val selectionListener = { id: Long ->
    updateSelection(id)
    for (selectionListener in selectionListeners) {
      selectionListener.invoke(id)
    }
  }

  val selectedItemId
    get() = selectedItem.value?.second

  private var list: List<Pair<Boolean, Account>> = emptyList()

  init {
    selectedItem.value = Pair(-1, -1)

    liveData.zip(selectedItem).observe(owner, Observer { it ->
      val list = it?.first
      val oldSelected = it?.second?.first ?: -1
      val selected = it?.second?.second ?: -1

      val oldList = this.list
      val newList: List<Pair<Boolean, Account>> = list.orEmpty().map {
        Pair(selected == it.id, it)
      }
      this.list = newList

      DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          val oldItem = oldList[oldItemPosition].second
          val newItem = newList[newItemPosition].second

          return oldItem.id == newItem.id
        }

        override fun getOldListSize(): Int {
          return oldList.size
        }

        override fun getNewListSize(): Int {
          return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          val oldItem = oldList[oldItemPosition].second
          val newItem = newList[newItemPosition].second

          return oldItem == newItem &&
                 oldItem.id != selected &&
                 newItem.id != selected &&
                 oldItem.id != oldSelected &&
                 newItem.id != oldSelected
        }
      }).dispatchUpdatesTo(this)
    })
  }

  private val addListener = {
    for (addListener in addListeners) {
      addListener.invoke()
    }
  }

  fun addEditListener(f: (Long) -> Unit) {
    actionListeners.add(f)
  }

  fun addAddListener(f: () -> Unit) {
    addListeners.add(f)
  }

  fun addSelectionListener(f: (Long) -> Unit) {
    selectionListeners.add(f)
  }

  override fun onBindViewHolder(holder: AccountViewHolder,
                                @SuppressLint("RecyclerView") position: Int) {
    when (holder) {
      is AccountViewHolder.Item -> {
        val item = list[position]
        holder.bind(item.second, item.first)
      }
      is AccountViewHolder.Add  -> {
      }
    }
  }

  override fun getItemViewType(position: Int) = when (position) {
    list.size -> TYPE_ADD
    else      -> TYPE_ACCOUNT
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(
      when (viewType) {
        TYPE_ADD -> R.layout.widget_core_account_add
        else     -> R.layout.widget_core_account
      }, parent, false
    )
    return when (viewType) {
      TYPE_ADD -> AccountViewHolder.Add(
        view, addListener
      )
      else     -> AccountViewHolder.Item(
        view, actionListener, clickListener
      )
    }
  }

  override fun getItemCount(): Int {
    return list.size + 1
  }

  companion object {
    private const val TYPE_ACCOUNT = 0
    private const val TYPE_ADD = 1
  }

  fun selectAccount(id: Long) {
    selectionListener(id)
  }

  interface ItemListener {
    fun onAction(id: Long, pos: Int)
  }

  sealed class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var data: Account? = null

    class Item(itemView: View, actionListener: ItemListener, clickListener: ItemListener)
      : AccountViewHolder(itemView) {
      @BindView(R.id.account_name)
      lateinit var accountName: TextView

      @BindView(R.id.account_description)
      lateinit var accountDescription: TextView

      @BindView(R.id.account_select)
      lateinit var accountSelect: AppCompatRadioButton

      @BindView(R.id.account_edit)
      lateinit var accountEdit: AppCompatImageButton

      init {
        ButterKnife.bind(this, itemView)
        accountEdit.setOnClickListener {
          actionListener.onAction(data?.id ?: -1L, adapterPosition)
        }
        itemView.setOnClickListener {
          clickListener.onAction(data?.id ?: -1L, adapterPosition)
        }
      }

      fun bind(account: Account, selected: Boolean) {
        data = account
        accountName.text = account.name
        accountDescription.text = itemView.context.resources.getString(
          R.string.label_user_on_host, account.user, account.host, account.port
        )
        accountSelect.isChecked = selected
      }

      fun clear() {
        data = null
        accountName.text = ""
        accountDescription.text = ""
        accountSelect.isChecked = false
      }
    }

    class Add(itemView: View, clickListener: () -> Unit) : AccountViewHolder(itemView) {
      init {
        itemView.setOnClickListener {
          clickListener.invoke()
        }
      }
    }
  }
}
