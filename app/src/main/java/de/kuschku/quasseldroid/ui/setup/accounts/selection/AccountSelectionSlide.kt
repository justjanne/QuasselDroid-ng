/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.ui.setup.accounts.edit.AccountEditActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity.Companion.REQUEST_CREATE_FIRST
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity.Companion.REQUEST_CREATE_NEW
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupActivity
import javax.inject.Inject

class AccountSelectionSlide : SlideFragment() {
  @BindView(R.id.account_list)
  lateinit var accountList: RecyclerView

  @Inject
  lateinit var accountViewModel: AccountViewModel

  override fun isValid() = adapter?.selectedItemId ?: -1L != -1L

  override val title = R.string.slide_account_select_title
  override val description = R.string.slide_account_select_description

  override fun setData(data: Bundle) {
    if (data.containsKey("selectedAccount"))
      adapter?.selectAccount(data.getLong("selectedAccount"))
  }

  override fun getData(data: Bundle) {
    data.putLong("selectedAccount", adapter?.selectedItemId ?: -1L)
  }

  private var adapter: AccountAdapter? = null
  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_select_account, container, false)
    ButterKnife.bind(this, view)
    val firstObserver = object : Observer<List<AccountDatabase.Account>?> {
      override fun onChanged(t: List<AccountDatabase.Account>?) {
        if (t?.isEmpty() != false)
          startActivityForResult(
            AccountSetupActivity.intent(requireContext()),
            REQUEST_CREATE_FIRST
          )
        accountViewModel.accounts.removeObserver(this)
      }
    }
    accountViewModel.accounts.observe(this, firstObserver)
    accountList.layoutManager = LinearLayoutManager(context)
    accountList.itemAnimator = DefaultItemAnimator()
    val adapter = AccountAdapter(
      this, accountViewModel.accounts, accountViewModel.selectedItem
    )
    this.adapter = adapter
    accountList.adapter = adapter

    adapter.addAddListener {
      startActivityForResult(AccountSetupActivity.intent(requireContext()), -1)
    }
    adapter.addEditListener { id ->
      startActivityForResult(AccountEditActivity.intent(requireContext(), id), REQUEST_CREATE_NEW)
    }
    adapter.addSelectionListener {
      updateValidity()
    }

    return view
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CREATE_FIRST && resultCode == Activity.RESULT_CANCELED) {
      activity?.finish()
    }
  }
}
