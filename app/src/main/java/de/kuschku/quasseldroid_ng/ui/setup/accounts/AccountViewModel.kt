package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase

class AccountViewModel(application: Application) : AndroidViewModel(application) {
  private val database: AccountDatabase = AccountDatabase.Creator.init(
    getApplication()
  )
  val accounts: LiveData<PagedList<AccountDatabase.Account>> = database.accounts().all().create(
    0,
    PagedList.Config.Builder()
      .setPageSize(50)
      .setPrefetchDistance(50)
      .build()
  )
}
