package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.ui.setup.SetupActivity
import org.threeten.bp.Instant

class AccountSetupActivity : SetupActivity() {
  private val thread = HandlerThread("Setup")
  private lateinit var handler: Handler

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
      val (id) = AccountDatabase.Creator.init(this).accounts().create(account)
      runOnUiThread {
        setResult(Activity.RESULT_OK)
        finish()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    thread.start()
    handler = Handler(thread.looper)
    super.onCreate(savedInstanceState)
  }

  override fun onDestroy() {
    handler.post { thread.quit() }
    super.onDestroy()
  }

  override val fragments = listOf(
    AccountSetupConnectionSlide(),
    AccountSetupUserSlide(),
    AccountSetupNameSlide()
  )
}
