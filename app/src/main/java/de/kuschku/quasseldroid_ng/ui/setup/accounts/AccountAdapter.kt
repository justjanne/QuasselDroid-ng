package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.annotation.SuppressLint
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
  private val selectionListeners = mutableSetOf<(Long) -> Unit>()

  private val clickListener = object : ItemListener {
    override fun onAction(id: Long, pos: Int) {
      notifySelectionChanged(selectedItemView, pos)
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

  private val selectionListener = { id: Long ->
    selectedItemId = id
    for (selectionListener in selectionListeners) {
      selectionListener.invoke(id)
    }
  }

  private var selectedItemView = -1
  var selectedItemId = -1L
    private set

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
        val account = getItem(position)
        if (account == null) {
          holder.clear()
        } else {
          val selected = account.id == selectedItemId
          if (selected) {
            selectedItemView = position
          }
          holder.bind(account, selected)
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

  fun selectAccount(id: Long) {
    selectedItemView = -1
    selectionListener(id)
  }

  fun notifySelectionChanged(from: Int?, to: Int?) {
    val _from = from ?: -1
    val _to = to ?: -1

    if (_from != -1)
      notifyItemChanged(_from)

    selectedItemView = _to

    if (_to != -1)
      notifyItemChanged(_to)
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
