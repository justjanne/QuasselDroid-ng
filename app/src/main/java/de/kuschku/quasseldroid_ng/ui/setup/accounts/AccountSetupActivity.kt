package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Activity
import android.os.Bundle
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.ui.setup.SetupActivity
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import org.threeten.bp.Instant

class AccountSetupActivity : SetupActivity() {
  private val handler = AndroidHandlerThread("Setup")

  override fun onDone(data: Bundle) {
    val account = AccountDatabase.Account(
      id = 0,
      host = data.getString("host"),
      port = data.getInt("port"),
      user = data.getString("user"),
      pass = data.getString("pass"),
      name = data.getString("name"),
      lastUsed = Instant.now().epochSecond
    )
    handler.post {
      AccountDatabase.Creator.init(this).accounts().create(account)
      runOnUiThread {
        setResult(Activity.RESULT_OK)
        finish()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }

  override val fragments = listOf(
    AccountSetupConnectionSlide(),
    AccountSetupUserSlide(),
    AccountSetupNameSlide()
  )
}
