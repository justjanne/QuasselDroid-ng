package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import de.kuschku.quasseldroid.persistence.AccountDatabase

class AccountViewModel(application: Application) : AndroidViewModel(application) {
  private val database: AccountDatabase = AccountDatabase.Creator.init(
    getApplication()
  )
  val accounts: LiveData<List<AccountDatabase.Account>> = database.accounts().all()
  val selectedItem = MutableLiveData<Pair<Long, Long>>()
}
