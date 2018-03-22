package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.ui.setup.SetupActivity
import de.kuschku.quasseldroid.util.helper.editCommit

class AccountSelectionActivity : SetupActivity() {
  companion object {
    const val REQUEST_CREATE_FIRST = 0
    const val REQUEST_CREATE_NEW = 1
  }

  override val fragments = listOf(
    AccountSelectionSlide()
  )

  private lateinit var statusPreferences: SharedPreferences
  override fun onDone(data: Bundle) {
    statusPreferences.editCommit {
      putLong(Keys.Status.selectedAccount, data.getLong(Keys.Status.selectedAccount, -1))
      putBoolean(Keys.Status.reconnect, true)
    }
    setResult(Activity.RESULT_OK)
    finish()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statusPreferences = this.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
    val data = Bundle()
    val selectedAccount = statusPreferences.getLong(Keys.Status.selectedAccount, -1)
    data.putLong(Keys.Status.selectedAccount, selectedAccount)
    setInitData(data)
  }
}
