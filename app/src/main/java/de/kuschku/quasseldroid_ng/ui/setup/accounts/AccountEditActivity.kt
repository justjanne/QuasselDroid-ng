package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase

class AccountEditActivity : AppCompatActivity() {
  @BindView(R.id.name)
  lateinit var name: TextView

  @BindView(R.id.host)
  lateinit var host: TextView

  @BindView(R.id.port)
  lateinit var port: TextView

  @BindView(R.id.user)
  lateinit var user: TextView

  @BindView(R.id.pass)
  lateinit var pass: TextView

  @BindView(R.id.save_button)
  lateinit var saveButton: FloatingActionButton

  private var accountId: Long = -1
  private var account: AccountDatabase.Account? = null
  lateinit var database: AccountDatabase

  private val thread = HandlerThread("AccountEdit")
  private lateinit var handler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    thread.start()
    handler = Handler(thread.looper)

    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.setup_account_edit)
    ButterKnife.bind(this)

    saveButton.setOnClickListener {
      val it = account
      if (it != null) {
        it.name = name.text.toString()
        it.host = host.text.toString()
        it.port = port.text.toString().toIntOrNull() ?: 4242
        it.user = user.text.toString()
        it.pass = pass.text.toString()
        handler.post {
          database.accounts().save(it)
          setResult(Activity.RESULT_OK)
          finish()
        }
      }
    }

    database = AccountDatabase.Creator.init(this)
    handler.post {
      accountId = intent.getLongExtra("account", -1)
      if (accountId == -1L) {
        setResult(Activity.RESULT_CANCELED)
        finish()
      }
      account = database.accounts().findById(accountId)
      if (account == null) {
        setResult(Activity.RESULT_CANCELED)
        finish()
      }

      name.text = account?.name
      host.text = account?.host
      port.text = account?.port?.toString()
      user.text = account?.user
      pass.text = account?.pass
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.setup_edit_account, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onDestroy() {
    handler.post { thread.quit() }
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.delete -> {
      AlertDialog.Builder(this)
        .setTitle("Delete?")
        .setMessage("Are you sure?")
        .setPositiveButton("Delete") { _, _ ->
          val it = account
          if (it != null)
            handler.post {
              database.accounts().delete(it)
            }
          setResult(Activity.RESULT_OK)
          finish()
        }
        .setNegativeButton("Cancel") { dialogInterface, _ ->
          dialogInterface.cancel()
        }
        .show()
      true
    }
    else        -> super.onOptionsItemSelected(item)
  }
}
