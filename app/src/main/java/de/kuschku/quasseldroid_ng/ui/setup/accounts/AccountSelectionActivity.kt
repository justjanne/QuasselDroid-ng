package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import de.kuschku.quasseldroid_ng.ui.setup.SetupActivity
import de.kuschku.quasseldroid_ng.util.helper.editCommit

class AccountSelectionActivity : SetupActivity() {
  override val fragments = listOf(
    AccountSelectionSlide()
  )

  lateinit var statusPreferences: SharedPreferences
  override fun onDone(data: Bundle) {
    statusPreferences.editCommit {
      putLong(selectedAccountKey, data.getLong(selectedAccountKey, -1))
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statusPreferences = this.getSharedPreferences("status", Context.MODE_PRIVATE)
    val data = Bundle()
    data.putLong(selectedAccountKey, statusPreferences.getLong(selectedAccountKey, -1))
    setInitData(data)
  }

  companion object {
    private const val selectedAccountKey = "selectedAccount"
  }
}
