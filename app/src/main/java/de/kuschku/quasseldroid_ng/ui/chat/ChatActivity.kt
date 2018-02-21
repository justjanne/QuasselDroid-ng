package de.kuschku.quasseldroid_ng.ui.chat

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.SettingsActivity
import de.kuschku.quasseldroid_ng.ui.settings.data.BacklogSettings
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.*
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid_ng.util.ui.MaterialContentLoadingProgressBar

class ChatActivity : ServiceBoundActivity() {
  @BindView(R.id.drawerLayout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.progressBar)
  lateinit var progressBar: MaterialContentLoadingProgressBar

  @BindView(R.id.buttonSend)
  lateinit var buttonSend: Button

  @BindView(R.id.input)
  lateinit var input: EditText

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var viewModel: QuasselViewModel

  private var snackbar: Snackbar? = null

  private lateinit var database: QuasselDatabase

  private var backlogSettings = BacklogSettings()

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    viewModel = ViewModelProviders.of(this)[QuasselViewModel::class.java]
    viewModel.setBackend(this.backend)

    database = QuasselDatabase.Creator.init(application)

    setSupportActionBar(toolbar)

    viewModel.getBuffer().observe(
      this, Observer {
      if (it != null) {
        drawerLayout.closeDrawer(Gravity.START, true)
      }
    }
    )

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    drawerToggle = ActionBarDrawerToggle(
      this,
      drawerLayout,
      R.string.drawer_open,
      R.string.drawer_close
    )
    drawerToggle.syncState()

    backend.observeSticky(
      this, Observer { backendValue ->
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
        }
      }
    }
    )

    buttonSend.setOnClickListener {
      viewModel.session { session ->
        viewModel.getBuffer().let { bufferId ->
          session.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
            session.rpcHandler?.sendInput(bufferInfo, input.text.toString())
          }
        }
      }
      input.text.clear()
    }

    viewModel.connectionState.observe(
      this, Observer {
      val status = it ?: ConnectionState.DISCONNECTED

      if (status == ConnectionState.CONNECTED) {
        progressBar.progress = 1
        progressBar.max = 1
      } else {
        progressBar.isIndeterminate = status != ConnectionState.INIT
      }

      progressBar.toggle(
        status != ConnectionState.CONNECTED && status != ConnectionState.DISCONNECTED
      )

      snackbar?.dismiss()
      snackbar = Snackbar.make(
        findViewById(R.id.contentMessages),
        status.name,
        Snackbar.LENGTH_SHORT
      )
      snackbar?.show()
    }
    )

    viewModel.initState.observe(
      this, Observer {
      val (progress, max) = it ?: 0 to 0

      progressBar.max = max
      progressBar.progress = progress
    }
    )
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putInt("OPEN_BUFFER", viewModel.getBuffer().value ?: -1)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    viewModel.setBuffer(savedInstanceState?.getInt("OPEN_BUFFER", -1) ?: -1)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home -> {
      drawerToggle.onOptionsItemSelected(item)
    }
    R.id.clear        -> {
      handler.post {
        viewModel.sessionManager { manager ->
          viewModel.getBuffer().let { buffer ->
            manager.backlogStorage.clearMessages(buffer)
            manager.backlogManager?.requestBacklog(
              bufferId = buffer,
              last = -1,
              limit = backlogSettings.dynamicAmount
            )
          }
        }
      }
      true
    }
    R.id.settings     -> {
      startActivity(Intent(applicationContext, SettingsActivity::class.java))
      true
    }
    R.id.disconnect   -> {
      handler.post {
        getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editApply {
          putBoolean(Keys.Status.reconnect, false)
        }
        backend()?.disconnect(true)
        setResult(Activity.RESULT_OK)
        finish()
      }
      true
    }
    else              -> super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}
