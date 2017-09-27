package de.kuschku.quasseldroid_ng.ui

import android.app.Activity
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.service.QuasselService
import de.kuschku.quasseldroid_ng.util.helper.editApply
import de.kuschku.quasseldroid_ng.util.helper.stickyMapNotNull
import de.kuschku.quasseldroid_ng.util.helper.stickySwitchMapNotNull
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class ChatActivity : ServiceBoundActivity() {
  @BindView(R.id.connect)
  lateinit var connect: Button

  @BindView(R.id.disconnect)
  lateinit var disconnect: Button

  @BindView(R.id.clear)
  lateinit var clear: Button

  @BindView(R.id.errorList)
  lateinit var errorList: TextView

  private val thread = HandlerThread("Chat")
  private lateinit var handler: Handler

  private val state = backend.stickyMapNotNull(null, Backend::sessionManager)
    .stickySwitchMapNotNull(ConnectionState.DISCONNECTED) { session ->
      LiveDataReactiveStreams.fromPublisher(session.state)
    }

  private var snackbar: Snackbar? = null

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
  private val logHandler = object : LoggingHandler() {
    override fun log(logLevel: LogLevel, tag: String, message: String?, throwable: Throwable?) {
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
      = (logLevel.ordinal >= LogLevel.INFO.ordinal)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    errorList.text = savedInstanceState?.getString("log", "") ?: ""
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putString("log", errorList.text.toString())
    super.onSaveInstanceState(outState)
  }

  var account: AccountDatabase.Account? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    thread.start()
    handler = Handler(thread.looper)

    startService(Intent(this, QuasselService::class.java))
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    val database = AccountDatabase.Creator.init(this)
    handler.post {
      val accountId = getSharedPreferences("status", Context.MODE_PRIVATE)
        ?.getLong(selectedAccountKey, -1) ?: -1
      if (accountId == -1L) {
        setResult(Activity.RESULT_CANCELED)
        finish()
      }
      val it = database.accounts().findById(accountId)
      if (it == null) {
        setResult(Activity.RESULT_CANCELED)
        finish()
      }
      account = it
    }

    connect.setOnClickListener {
      val account = account
      if (account != null)
        backend.value?.connect(
          SocketAddress(account.host, account.port.toShort()),
          account.user,
          account.pass
        )
    }

    disconnect.setOnClickListener {
      backend.value?.disconnect()
    }

    clear.setOnClickListener {
      errorList.text = ""
    }

    state.observe(this, Observer {
      val status = it ?: ConnectionState.DISCONNECTED
      val disconnected = status == ConnectionState.DISCONNECTED

      disconnect.isEnabled = !disconnected
      connect.isEnabled = disconnected

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
      getSharedPreferences("status", Context.MODE_PRIVATE).editApply {
        putBoolean("reconnect", false)
      }
      finish()
      true
    }
    else            -> super.onOptionsItemSelected(item)
  }

  override fun onStart() {
    super.onStart()
    LoggingHandler.loggingHandlers.add(logHandler)
  }

  override fun onStop() {
    LoggingHandler.loggingHandlers.remove(logHandler)
    super.onStop()
  }

  companion object {
    private const val selectedAccountKey = "selectedAccount"
  }
}
