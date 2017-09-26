package de.kuschku.quasseldroid_ng.ui

import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.stickyMapNotNull
import de.kuschku.quasseldroid_ng.util.helper.stickySwitchMapNotNull
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class MainActivity : ServiceBoundActivity() {
  @BindView(R.id.host)
  lateinit var host: EditText

  @BindView(R.id.port)
  lateinit var port: EditText

  @BindView(R.id.user)
  lateinit var user: EditText

  @BindView(R.id.pass)
  lateinit var pass: EditText

  @BindView(R.id.connect)
  lateinit var connect: Button

  @BindView(R.id.disconnect)
  lateinit var disconnect: Button

  @BindView(R.id.clear)
  lateinit var clear: Button

  @BindView(R.id.errorList)
  lateinit var errorList: TextView

  private val state = backend.stickyMapNotNull(null, Backend::sessionManager)
    .stickySwitchMapNotNull(ConnectionState.DISCONNECTED) { session ->
      LiveDataReactiveStreams.fromPublisher(session.state)
    }

  private var snackbar: Snackbar? = null

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
  private val handler = object : LoggingHandler() {
    override fun log(logLevel: LogLevel, tag: String, message: String?, throwable: Throwable?) {
      if (logLevel.ordinal < LogLevel.INFO.ordinal)
        return
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

    override fun isLoggable(logLevel: LogLevel, tag: String) = true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    connect.setOnClickListener {
      backend.value?.connect(
        SocketAddress(host.text.toString(), port.text.toString().toShort()),
        user.text.toString(),
        pass.text.toString()
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
    else -> super.onOptionsItemSelected(item)
  }

  override fun onStart() {
    super.onStart()
    LoggingHandler.loggingHandlers.add(handler)
  }

  override fun onStop() {
    LoggingHandler.loggingHandlers.remove(handler)
    super.onStop()
  }
}
