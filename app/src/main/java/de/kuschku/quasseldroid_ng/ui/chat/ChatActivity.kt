package de.kuschku.quasseldroid_ng.ui.chat

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
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
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.*
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid_ng.util.ui.MaterialContentLoadingProgressBar

class ChatActivity : ServiceBoundActivity() {
  private var contentMessages: MessageListFragment? = null
  private var chatListFragment: BufferViewConfigFragment? = null
  private var nickListFragment: NickListFragment? = null
  private var toolbarFragment: ToolbarFragment? = null

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

  private val sessionManager: LiveData<SessionManager?> = backend.map(Backend::sessionManager)
  private val state = sessionManager.switchMapRx(SessionManager::state)
  private val initStatus = sessionManager.switchMapRx(SessionManager::initStatus)

  private var snackbar: Snackbar? = null

  private val currentBuffer = MutableLiveData<BufferId>()

  private lateinit var database: QuasselDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    database = QuasselDatabase.Creator.init(application)

    contentMessages = supportFragmentManager.findFragmentById(
      R.id.contentMessages
    ) as? MessageListFragment
    chatListFragment = supportFragmentManager.findFragmentById(
      R.id.chatListFragment
    ) as? BufferViewConfigFragment
    nickListFragment = supportFragmentManager.findFragmentById(
      R.id.nickListFragment
    ) as? NickListFragment
    toolbarFragment = supportFragmentManager.findFragmentById(
      R.id.toolbarFragment
    ) as? ToolbarFragment

    setSupportActionBar(toolbar)

    chatListFragment?.currentBuffer?.value = currentBuffer
    nickListFragment?.currentBuffer?.value = currentBuffer
    contentMessages?.currentBuffer?.value = currentBuffer
    toolbarFragment?.currentBuffer?.value = currentBuffer

    chatListFragment?.clickListeners?.add {
      currentBuffer.value = it
      println("Changed buffer to $it")
    }

    currentBuffer.observe(
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
      sessionManager { sessionManager ->
        currentBuffer { bufferId ->
          sessionManager.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
            sessionManager.rpcHandler?.sendInput(bufferInfo, input.text.toString())
          }
        }
      }
      input.text.clear()
    }

    state.observe(
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

    initStatus.observe(
      this, Observer {
      val (progress, max) = it ?: 0 to 0

      progressBar.max = max
      progressBar.progress = progress
    }
    )
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putInt("OPEN_BUFFER", currentBuffer.value ?: -1)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    currentBuffer.value = savedInstanceState?.getInt("OPEN_BUFFER", -1)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home -> {
      drawerToggle.onOptionsItemSelected(item)
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
