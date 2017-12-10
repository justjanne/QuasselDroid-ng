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
import de.kuschku.quasseldroid_ng.util.helper.editApply
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.observeSticky
import de.kuschku.quasseldroid_ng.util.helper.switchMapRx
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundActivity

class ChatActivity : ServiceBoundActivity() {
  var contentMessages: MessageListFragment? = null
  var chatListFragment: BufferViewConfigFragment? = null

  @BindView(R.id.drawerLayout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.buttonSend)
  lateinit var buttonSend: Button

  @BindView(R.id.input)
  lateinit var input: EditText

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private val handler = AndroidHandlerThread("Chat")

  private val sessionManager: LiveData<SessionManager?> = backend.map(Backend::sessionManager)
  private val state = sessionManager.switchMapRx(SessionManager::state)

  private var snackbar: Snackbar? = null

  private val currentBuffer = MutableLiveData<BufferId>()

  private lateinit var database: QuasselDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    database = QuasselDatabase.Creator.init(application)

    contentMessages = supportFragmentManager.findFragmentById(R.id.contentMessages) as? MessageListFragment
    chatListFragment = supportFragmentManager.findFragmentById(R.id.chatListFragment) as? BufferViewConfigFragment

    setSupportActionBar(toolbar)

    chatListFragment?.currentBuffer?.value = currentBuffer
    contentMessages?.currentBuffer?.value = currentBuffer

    chatListFragment?.clickListeners?.add {
      currentBuffer.value = it
      println("Changed buffer to $it")
    }

    //drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
    //actionBar.setDisplayHomeAsUpEnabled(true)
    //actionBar.setHomeButtonEnabled(true)

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
        }
      }
    })

    buttonSend.setOnClickListener {
      sessionManager.value?.also { sessionManager ->
        currentBuffer.value?.also { bufferId ->
          sessionManager.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
            sessionManager.rpcHandler?.sendInput(bufferInfo, input.text.toString())
          }
        }
      }
      input.text.clear()
    }

    state.observe(this, Observer {
      val status = it ?: ConnectionState.DISCONNECTED

      snackbar?.dismiss()
      snackbar = Snackbar.make(findViewById(R.id.contentMessages), status.name, Snackbar.LENGTH_SHORT)
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
        backend.value?.disconnect(true)
        stopService()
        setResult(Activity.RESULT_OK)
        finish()
      }
      true
    }
    R.id.loadMore -> handler.post {
      currentBuffer.value?.also { bufferId ->
        sessionManager.value?.apply {
          backlogManager?.requestBacklog(
            bufferId = bufferId,
            last = database.message().findFirstByBufferId(bufferId)?.messageId ?: -1,
            limit = 20
          )
        }
      }
    }
    R.id.clear -> handler.post {
      currentBuffer.value?.also { bufferId ->
        sessionManager.value?.apply {
          backlogStorage.clearMessages(bufferId)
        }
      }
    }
    else            -> super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}
