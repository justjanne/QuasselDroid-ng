package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.util.Patterns
import de.kuschku.quasseldroid_ng.util.TextValidator
import de.kuschku.quasseldroid_ng.util.helper.editCommit

class AccountEditActivity : AppCompatActivity() {
  @BindView(R.id.nameWrapper)
  lateinit var nameWrapper: TextInputLayout
  @BindView(R.id.name)
  lateinit var name: TextInputEditText

  @BindView(R.id.hostWrapper)
  lateinit var hostWrapper: TextInputLayout
  @BindView(R.id.host)
  lateinit var host: TextInputEditText

  @BindView(R.id.portWrapper)
  lateinit var portWrapper: TextInputLayout
  @BindView(R.id.port)
  lateinit var port: TextInputEditText

  @BindView(R.id.userWrapper)
  lateinit var userWrapper: TextInputLayout
  @BindView(R.id.user)
  lateinit var user: TextInputEditText

  @BindView(R.id.passWrapper)
  lateinit var passWrapper: TextInputLayout
  @BindView(R.id.pass)
  lateinit var pass: TextInputEditText

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

      name.setText(account?.name)
      host.setText(account?.host)
      port.setText(account?.port?.toString())
      user.setText(account?.user)
      pass.setText(account?.pass)
    }

    nameValidator = object : TextValidator(
      nameWrapper::setError, resources.getString(R.string.hintInvalidName)
    ) {
      override fun validate(text: Editable)
        = text.isNotBlank()
    }

    hostValidator = object : TextValidator(
      hostWrapper::setError, resources.getString(R.string.hintInvalidHost)
    ) {
      override fun validate(text: Editable)
        = text.toString().matches(Patterns.DOMAIN_NAME.toRegex())
    }

    portValidator = object : TextValidator(
      portWrapper::setError, resources.getString(R.string.hintInvalidPort)
    ) {
      override fun validate(text: Editable)
        = text.toString().toIntOrNull() in (0 until 65536)
    }

    userValidator = object : TextValidator(
      userWrapper::setError, resources.getString(R.string.hintInvalidUser)
    ) {
      override fun validate(text: Editable)
        = text.isNotBlank()
    }

    name.addTextChangedListener(nameValidator)
    host.addTextChangedListener(hostValidator)
    port.addTextChangedListener(portValidator)
    user.addTextChangedListener(userValidator)
    nameValidator.afterTextChanged(name.text)
    hostValidator.afterTextChanged(host.text)
    portValidator.afterTextChanged(port.text)
    userValidator.afterTextChanged(user.text)
  }

  private lateinit var nameValidator: TextValidator
  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
  private lateinit var userValidator: TextValidator

  private val isValid
    get() = nameValidator.isValid && hostValidator.isValid && portValidator.isValid
      && userValidator.isValid

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
              val preferences = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
              if (preferences.getLong(Keys.Status.selectedAccount, -1) == it.id) {
                preferences.editCommit {
                  remove(Keys.Status.selectedAccount)
                }
              }
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
    R.id.save   -> {
      if (isValid) {
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
      true
    }
    else        -> super.onOptionsItemSelected(item)
  }
}
