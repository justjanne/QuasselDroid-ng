package de.kuschku.quasseldroid_ng.ui

import android.arch.lifecycle.LiveData
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
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.session.Backend
import de.kuschku.quasseldroid_ng.session.ConnectionState
import de.kuschku.quasseldroid_ng.session.Session
import de.kuschku.quasseldroid_ng.session.SocketAddress
import de.kuschku.quasseldroid_ng.util.helpers.Logger
import de.kuschku.quasseldroid_ng.util.helpers.stickyMapNotNull
import de.kuschku.quasseldroid_ng.util.helpers.stickySwitchMapNotNull
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

  private val status: LiveData<ConnectionState>
    = stickySwitchMapNotNull(backend, Backend::status, ConnectionState.DISCONNECTED)
  private val session: LiveData<Session?>
    = stickyMapNotNull(backend, Backend::session, null)

  private var snackbar: Snackbar? = null

  private val handler = { tag: String, message: String?, throwable: Throwable? ->
    runOnUiThread {
      errorList.append(DateTimeFormatter.ISO_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
      errorList.append(" ")
      errorList.append(tag)
      errorList.append(": ")
      errorList.append(message)
      errorList.append("\n")
      if (throwable != null) {
        errorList.append(Log.getStackTraceString(throwable))
        errorList.append("\n")
      }
    }
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

    status.observe(this, Observer {
      val disconnected = it == ConnectionState.DISCONNECTED
      disconnect.isEnabled = !disconnected
      connect.isEnabled = disconnected

      snackbar?.dismiss()
      snackbar = Snackbar.make(errorList, it!!.name, Snackbar.LENGTH_SHORT)
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
    Logger.handler = handler
  }

  override fun onStop() {
    Logger.handler = null
    super.onStop()
  }
}
