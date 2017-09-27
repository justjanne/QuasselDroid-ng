package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.ui.setup.SlideFragment

class AccountSelectionSlide : SlideFragment() {
  @BindView(R.id.account_list)
  lateinit var accountList: RecyclerView

  override fun isValid() = adapter.selectedPos.value ?: -1 != -1

  override val title = R.string.slideAccountSelectTitle
  override val description = R.string.slideAccountSelectDescription

  override fun setData(data: Bundle) {
    if (data.containsKey("selectedAccount"))
      adapter.selectAccount(data.getLong("selectedAccount"))
  }

  override fun getData(data: Bundle) {
    data.putLong("selectedAccount", adapter.selectedItemId)
  }

  private val adapter = AccountAdapter()
  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_select_account, container, false)
    ButterKnife.bind(this, view)
    val accountViewmodel = ViewModelProviders.of(this).get(
      AccountViewModel::class.java)
    val firstObserver = object : Observer<PagedList<AccountDatabase.Account>?> {
      override fun onChanged(t: PagedList<AccountDatabase.Account>?) {
        if (t?.isEmpty() != false)
          startActivityForResult(Intent(context, AccountSetupActivity::class.java), -1)
        accountViewmodel.accounts.removeObserver(this)
      }
    }
    accountViewmodel.accounts.observe(this, firstObserver)
    accountViewmodel.accounts.observe(this, Observer(adapter::setList))
    accountList.layoutManager = LinearLayoutManager(context)
    accountList.itemAnimator = DefaultItemAnimator()
    accountList.adapter = adapter
    adapter.selectedPos.observe(this, Observer {
      updateValidity()
    })
    adapter.addAddListener {
      startActivityForResult(Intent(context, AccountSetupActivity::class.java), -1)
    }
    adapter.addEditListener { id ->
      val intent = Intent(context, AccountEditActivity::class.java)
      intent.putExtra("account", id)
      startActivityForResult(intent, -1)
    }

    return view
  }

}
