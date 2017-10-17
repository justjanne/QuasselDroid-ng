package de.kuschku.quasseldroid_ng.ui.chat

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.editApply
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.observeSticky
import de.kuschku.quasseldroid_ng.util.helper.switchMapRx
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundActivity
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class ChatActivity : ServiceBoundActivity() {
  @BindView(R.id.clear)
  lateinit var clear: Button

  @BindView(R.id.errorList)
  lateinit var errorList: TextView

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  private val handler = AndroidHandlerThread("Chat")

  private val sessionManager: LiveData<SessionManager?> = backend.map(Backend::sessionManager)
  private val state = sessionManager.switchMapRx(SessionManager::state)

  private var snackbar: Snackbar? = null

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
  private val logHandler = object : LoggingHandler() {
    override fun log(logLevel: LogLevel, tag: String, message: String?,
                     throwable: Throwable?) {
      val time = dateTimeFormatter.format(ZonedDateTime.now(ZoneOffset.UTC))
      runOnUiThread {
        errorList.append("$time $tag: ")
        if (message != null) {
          errorList.append(message)
        }
        if (throwable != null) {
          errorList.append("\n")
          errorList.append(Log.getStackTraceString(throwable))
        }
        errorList.append("\n")
      }
    }

    override fun isLoggable(logLevel: LogLevel, tag: String)
      = (logLevel.ordinal >= INFO.ordinal)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    errorList.text = savedInstanceState?.getString("log", "") ?: ""
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putString("log", errorList.text.toString())
    super.onSaveInstanceState(outState)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)
    setSupportActionBar(toolbar)

    backend.observeSticky(this, Observer { backendValue ->
      if (backendValue != null) {
        val database = AccountDatabase.Creator.init(this)
        handler.post {
          val accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
            ?.getLong(Keys.Status.selectedAccount, -1) ?: -1
          if (accountId == -1L) {
            setResult(Activity.RESULT_OK)
            finish()
          }
          val account = database.accounts().findById(accountId)
          if (account == null) {
            setResult(Activity.RESULT_OK)
            finish()
          } else {
            backendValue.connectUnlessConnected(
              SocketAddress(account.host, account.port),
              account.user,
              account.pass,
              true
            )
          }
          CrashHandler.handle(
            IllegalArgumentException(
              "WRONG!",
              RuntimeException(
                "WRONG!",
                NullPointerException(
                  "Super wrong!"
                )
              )
            )
          )
        }
      }
    })

    clear.setOnClickListener {
      errorList.text = ""
    }

    state.observeSticky(this, Observer {
      val status = it ?: ConnectionState.DISCONNECTED

      snackbar?.dismiss()
      snackbar = Snackbar.make(errorList, status.name, Snackbar.LENGTH_SHORT)
      snackbar?.show()
    })
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.disconnect -> {
      handler.post {
        getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editApply {
          putBoolean(Keys.Status.reconnect, false)
        }
        backend.value?.disconnect()
        setResult(Activity.RESULT_OK)
        finish()
      }
      true
    }
    else            -> super.onOptionsItemSelected(item)
  }

  override fun onStart() {
    super.onStart()
    LoggingHandler.loggingHandlers.add(logHandler)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }

  override fun onStop() {
    LoggingHandler.loggingHandlers.remove(logHandler)
    super.onStop()
  }
}
