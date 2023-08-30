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
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.ui.setup.accounts.edit.AccountEditActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity.Companion.REQUEST_CREATE_FIRST
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity.Companion.REQUEST_CREATE_NEW
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupActivity
import de.kuschku.quasseldroid.util.helper.map
import de.kuschku.quasseldroid.util.helper.observeSticky
import de.kuschku.quasseldroid.util.helper.zip
import javax.inject.Inject

class AccountSelectionSlide : SlideFragment() {
  lateinit var accountList: RecyclerView

  @Inject
  lateinit var accountViewModel: AccountViewModel

  override fun isValid() = accountViewModel.selectedItem.value?.isValidId() == true

  override val title = R.string.slide_account_select_title
  override val description = R.string.slide_account_select_description

  override fun setData(data: Bundle) {
    if (data.containsKey("selectedAccount")) {
      accountViewModel.selectedItem.postValue(AccountId(data.getLong("selectedAccount")))
    }
  }

  override fun getData(data: Bundle) {
    data.putLong("selectedAccount", accountViewModel.selectedItem.value?.id ?: -1L)
  }

  private val adapter = AccountAdapter()

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    BufferId
    val view = inflater.inflate(R.layout.setup_select_account, container, false)
    this.accountList = view.findViewById(R.id.account_list)
    val firstObserver = object : Observer<List<Account>?> {
      override fun onChanged(t: List<Account>?) {
        if (t?.isEmpty() != false)
          startActivityForResult(
            AccountSetupActivity.intent(requireContext()),
            REQUEST_CREATE_FIRST
          )
        accountViewModel.accounts.removeObserver(this)
      }
    }
    accountViewModel.accounts.observe(viewLifecycleOwner, firstObserver)
    accountList.layoutManager = LinearLayoutManager(context)
    accountList.itemAnimator = DefaultItemAnimator()
    accountList.adapter = adapter

    adapter.addAddListener {
      startActivityForResult(AccountSetupActivity.intent(requireContext()), -1)
    }
    adapter.addEditListener { id ->
      startActivityForResult(AccountEditActivity.intent(requireContext(), id), REQUEST_CREATE_NEW)
    }
    adapter.addClickListener {
      accountViewModel.selectedItem.postValue(it)
    }
    accountViewModel.selectedItem.observeSticky(viewLifecycleOwner) {
      updateValidity()
    }

    accountViewModel.accounts.zip(accountViewModel.selectedItem).map { (accounts, selected) ->
      accounts.map { Pair(it, it.id == selected) }
    }.observe(viewLifecycleOwner) {
      adapter.submitList((it ?: emptyList()) + Pair(null, false))
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
