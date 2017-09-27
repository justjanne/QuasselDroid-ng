package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import de.kuschku.quasseldroid_ng.ui.ChatActivity
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
      putBoolean("reconnect", true)
    }
    startActivity(Intent(this, ChatActivity::class.java))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statusPreferences = this.getSharedPreferences("status", Context.MODE_PRIVATE)
    val data = Bundle()
    val selectedAccount = statusPreferences.getLong(selectedAccountKey, -1)
    data.putLong(selectedAccountKey, selectedAccount)
    setInitData(data)

    if (statusPreferences.getBoolean("reconnect", false) && selectedAccount != -1L) {
      startActivity(Intent(this, ChatActivity::class.java))
    }
  }

  companion object {
    private const val selectedAccountKey = "selectedAccount"
  }
}
