package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.util.helper.zip

class AccountAdapter(
  owner: LifecycleOwner,
  val liveData: LiveData<List<AccountDatabase.Account>>,
  val selectedItem: MutableLiveData<Pair<Long, Long>>
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {
  private val actionListeners = mutableSetOf<(Long) -> Unit>()
  private val addListeners = mutableSetOf<() -> Unit>()
  private val selectionListeners = mutableSetOf<(Long) -> Unit>()

  private val clickListener = object : ItemListener {
    override fun onAction(id: Long, pos: Int) {
      selectionListener.invoke(id)
    }
  }

  private val actionListener = object : ItemListener {
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

  private var list: List<Pair<Boolean, AccountDatabase.Account>> = emptyList()

  init {
    selectedItem.value = Pair(-1, -1)

    liveData.zip(selectedItem).observe(owner, Observer { it ->
      val list = it?.first
      val oldSelected = it?.second?.first ?: -1
      val selected = it?.second?.second ?: -1

      val oldList = this.list
      val newList: List<Pair<Boolean, AccountDatabase.Account>> = list.orEmpty().map {
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

  fun removeEditListener(f: (Long) -> Unit) {
    actionListeners.remove(f)
  }

  fun addAddListener(f: () -> Unit) {
    addListeners.add(f)
  }

  fun removeAddListener(f: () -> Unit) {
    addListeners.remove(f)
  }

  fun addSelectionListener(f: (Long) -> Unit) {
    selectionListeners.add(f)
  }

  fun removeSelectionListener(f: (Long) -> Unit) {
    selectionListeners.remove(f)
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
      TYPE_ADD -> AccountViewHolder.Add(view, addListener)
      else     -> AccountViewHolder.Item(view, actionListener, clickListener)
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

      private var id = -1L

      init {
        ButterKnife.bind(this, itemView)
        accountEdit.setOnClickListener {
          actionListener.onAction(id, adapterPosition)
        }
        itemView.setOnClickListener {
          clickListener.onAction(id, adapterPosition)
        }
      }

      fun bind(account: AccountDatabase.Account, selected: Boolean) {
        id = account.id
        accountName.text = account.name
        accountDescription.text = itemView.context.resources.getString(
          R.string.userOnHost, account.user, account.host, account.port
        )
        accountSelect.isChecked = selected
      }

      fun clear() {
        id = -1L
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
