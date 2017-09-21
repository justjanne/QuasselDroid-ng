package de.kuschku.quasseldroid_ng.ui

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.stickyMapNotNull
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.logging.Handler
import java.util.logging.LogManager
import java.util.logging.LogRecord

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

  /*
  private val status: LiveData<ConnectionState>
    = stickySwitchMapNotNull(backend, Backend::status,
                                                                    ConnectionState.DISCONNECTED)
                                                                    */
  private val session: LiveData<Session?>
    = stickyMapNotNull(backend, Backend::session, null)

  private var snackbar: Snackbar? = null

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
  private val handler = object : Handler() {
    override fun publish(p0: LogRecord?) {
      if (p0 != null) {
        runOnUiThread {
          errorList.append(
            dateTimeFormatter.format(Instant.ofEpochMilli(p0.millis).atZone(ZoneOffset.UTC)))
          errorList.append(" ")
          errorList.append(p0.loggerName)
          errorList.append(": ")
          errorList.append(p0.message)
          errorList.append("\n")
        }
      }
    }

    override fun flush() {
    }

    override fun close() {
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

    /*
    status.observe(this, Observer {
      val disconnected = it == ConnectionState.DISCONNECTED
      disconnect.isEnabled = !disconnected
      connect.isEnabled = disconnected

      snackbar?.dismiss()
      snackbar = Snackbar.make(errorList, it!!.name, Snackbar.LENGTH_SHORT)
      snackbar?.show()
    })
    */
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
    LogManager.getLogManager().getLogger("").addHandler(handler)
  }

  override fun onStop() {
    LogManager.getLogManager().getLogger("").removeHandler(handler)
    super.onStop()
  }
}
