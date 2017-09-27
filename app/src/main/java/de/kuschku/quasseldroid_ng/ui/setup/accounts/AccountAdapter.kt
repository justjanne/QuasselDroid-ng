package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
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

class AccountAdapter :
  PagedListAdapter<AccountDatabase.Account, AccountAdapter.AccountViewHolder>(DIFF_CALLBACK) {
  private val actionListeners = mutableSetOf<(Long) -> Unit>()
  private val addListeners = mutableSetOf<() -> Unit>()
  val selectedItemId: Long
    get() {
      val position = selectedPos.value
      return if (position != null && position > -1 && position < super.getItemCount())
        getItem(position)?.id ?: -1
      else
        -1
    }

  private val clickListener = object : ItemListener {
    override fun onAction(id: Long, pos: Int) {
      changeSelection(id, pos)
    }
  }

  private val actionListener = object : ItemListener {
    override fun onAction(id: Long, pos: Int) {
      for (actionListener in actionListeners) {
        actionListener.invoke(id)
      }
    }
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

  override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
    when (holder) {
      is AccountViewHolder.Item -> {
        val account = getItem(position)
        if (account == null) {
          holder.clear()
        } else {
          val selected = selectedId == account.id
          holder.bind(account, position, selected)
          if (selected && position != selectedPos.value)
            holder.itemView.post {
              changeSelection(account.id, position)
            }
        }
      }
      is AccountViewHolder.Add  -> {
      }
    }
  }

  override fun getItemViewType(position: Int) = when (position) {
    super.getItemCount() -> TYPE_ADD
    else                 -> TYPE_ACCOUNT
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(
      when (viewType) {
        TYPE_ADD -> R.layout.widget_core_account_add
        else     -> R.layout.widget_core_account
      }, parent, false)
    return when (viewType) {
      TYPE_ADD -> AccountViewHolder.Add(view, addListener)
      else     -> AccountViewHolder.Item(view, actionListener, clickListener)
    }
  }

  override fun getItemCount(): Int {
    return super.getItemCount() + 1
  }

  companion object {
    private const val TYPE_ACCOUNT = 0
    private const val TYPE_ADD = 1

    private val DIFF_CALLBACK = object : DiffCallback<AccountDatabase.Account>() {
      override fun areContentsTheSame(oldItem: AccountDatabase.Account,
                                      newItem: AccountDatabase.Account): Boolean {
        return oldItem == newItem
      }

      override fun areItemsTheSame(oldItem: AccountDatabase.Account,
                                   newItem: AccountDatabase.Account): Boolean {
        return oldItem.id == newItem.id
      }
    }
  }

  private fun notifySelectionChanged(from: Int?, to: Int?) {
    if (from != null && from != -1)
      notifyItemChanged(from)

    val real_to = to ?: -1
    selectedPos.value = real_to

    if (to != null && to != -1)
      notifyItemChanged(to)
  }

  fun changeSelection(id: Long, position: Int) {
    notifySelectionChanged(selectedPos.value, position)
    selectedId = id
  }

  private fun indexOf(id: Long) = (0 until itemCount).lastOrNull {
    getItemViewType(it) == TYPE_ACCOUNT && getItem(it)?.id == id
  } ?: -1

  fun selectAccount(id: Long) {
    val index = indexOf(id)
    if (index != -1) {
      changeSelection(id, index)
    } else {
      selectedId = id
    }
  }

  private var selectedId = -1L
  var selectedPos = MutableLiveData<Int>()

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
      private var index = -1

      init {
        ButterKnife.bind(this, itemView)
        accountEdit.setOnClickListener {
          actionListener.onAction(id, index)
        }
        itemView.setOnClickListener {
          clickListener.onAction(id, index)
        }
      }

      fun bind(account: AccountDatabase.Account, position: Int, selected: Boolean) {
        index = position
        id = account.id
        accountName.text = account.name
        accountDescription.text = itemView.context.resources.getString(
          R.string.userOnHost, account.user, account.host, account.port
        )
        accountSelect.isChecked = selected
      }

      fun clear() {
        index = -1
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
