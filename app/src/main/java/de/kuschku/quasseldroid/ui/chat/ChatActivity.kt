package de.kuschku.quasseldroid.ui.chat

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.util.and
import de.kuschku.libquassel.util.or
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.ui.chat.input.Editor
import de.kuschku.quasseldroid.ui.chat.input.MessageHistoryAdapter
import de.kuschku.quasseldroid.ui.settings.app.AppSettingsActivity
import de.kuschku.quasseldroid.util.helper.editCommit
import de.kuschku.quasseldroid.util.helper.invoke
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.util.ui.MaterialContentLoadingProgressBar
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import javax.inject.Inject

class ChatActivity : ServiceBoundActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @BindView(R.id.drawer_layout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.progress_bar)
  lateinit var progressBar: MaterialContentLoadingProgressBar

  @BindView(R.id.editor_panel)
  lateinit var editorPanel: SlidingUpPanelLayout

  @BindView(R.id.history_panel)
  lateinit var historyPanel: SlidingUpPanelLayout

  @BindView(R.id.msg_history)
  lateinit var msgHistory: RecyclerView

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private lateinit var viewModel: QuasselViewModel

  @Inject
  lateinit var database: QuasselDatabase

  private lateinit var editor: Editor

  private val panelSlideListener: SlidingUpPanelLayout.PanelSlideListener = object :
    SlidingUpPanelLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View?, slideOffset: Float) = Unit

    override fun onPanelStateChanged(panel: View?,
                                     previousState: SlidingUpPanelLayout.PanelState?,
                                     newState: SlidingUpPanelLayout.PanelState?) {
      editor.setMultiLine(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    if (intent != null) {
      when {
        intent.type == "text/plain" -> {
          editor.formatHandler.replace(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    viewModel = ViewModelProviders.of(this)[QuasselViewModel::class.java]
    viewModel.backendWrapper.onNext(this.backend)

    editor = Editor(
      this,
      viewModel.autoCompleteData.toLiveData(),
      viewModel.lastWord,
      findViewById(R.id.chatline),
      findViewById(R.id.send),
      listOf(
        findViewById(R.id.autocomplete_list),
        findViewById(R.id.autocomplete_list_expanded)
      ),
      findViewById(R.id.formatting_menu),
      findViewById(R.id.formatting_toolbar),
      appearanceSettings,
      autoCompleteSettings,
      { lines ->
        viewModel.session { sessionOptional ->
          val session = sessionOptional.orNull()
          viewModel.buffer { bufferId ->
            session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
              val output = mutableListOf<IAliasManager.Command>()
              for ((stripped, formatted) in lines) {
                viewModel.addRecentlySentMessage(stripped)
                session.aliasManager?.processInput(bufferInfo, formatted, output)
              }
              for (command in output) {
                session.rpcHandler?.sendInput(command.buffer, command.message)
              }
            }
          }
        }
      },
      { expanded ->
        historyPanel.panelState = if (expanded)
          SlidingUpPanelLayout.PanelState.EXPANDED
        else
          SlidingUpPanelLayout.PanelState.COLLAPSED
      }
    )

    msgHistory.itemAnimator = DefaultItemAnimator()
    msgHistory.layoutManager = LinearLayoutManager(this)
    val messageHistoryAdapter = MessageHistoryAdapter { text ->
      editor.formatHandler.replace(text)
      historyPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }
    msgHistory.adapter = messageHistoryAdapter
    viewModel.recentlySentMessages_liveData.observe(this,
                                                    Observer(messageHistoryAdapter::submitList))

    setSupportActionBar(toolbar)

    viewModel.buffer_liveData.observe(this, Observer {
      if (it != null && drawerLayout.isDrawerOpen(Gravity.START)) {
        drawerLayout.closeDrawer(Gravity.START, true)
      }
    })

    // Don’t show a drawer toggle if in tablet landscape mode
    if (resources.getBoolean(R.bool.buffer_drawer_exists)) {
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
      drawerToggle = ActionBarDrawerToggle(
        this,
        drawerLayout,
        R.string.label_open,
        R.string.label_close
      )
      drawerToggle.syncState()
    }

    viewModel.errors_liveData.observe(this, Observer { optional ->
      optional?.orNull().let {
        when (it) {
          is HandshakeMessage.ClientInitReject  ->
            MaterialDialog.Builder(this)
              .title(R.string.label_error_init)
              .content(Html.fromHtml(it.errorString))
              .neutralText(R.string.label_close)
              .build()
              .show()
          is HandshakeMessage.CoreSetupReject   ->
            MaterialDialog.Builder(this)
              .title(R.string.label_error_setup)
              .content(Html.fromHtml(it.errorString))
              .neutralText(R.string.label_close)
              .build()
              .show()
          is HandshakeMessage.ClientLoginReject ->
            MaterialDialog.Builder(this)
              .title(R.string.label_error_login)
              .content(Html.fromHtml(it.errorString))
              .negativeText(R.string.label_disconnect)
              .positiveText("Change User/Password")
              .onNegative { _, _ ->
                disconnect()
              }
              .onPositive { _, _ ->
                MaterialDialog.Builder(this)
                  .title("Login Required")
                  .customView(R.layout.setup_account_user, false)
                  .negativeText(R.string.label_disconnect)
                  .positiveText(R.string.label_save)
                  .onNegative { _, _ ->
                    disconnect()
                  }
                  .onPositive { dialog, _ ->
                    dialog.customView?.run {
                      val userField = findViewById<EditText>(R.id.user)
                      val passField = findViewById<EditText>(R.id.pass)

                      val user = userField.text.toString()
                      val pass = passField.text.toString()

                      backend.value.orNull()?.updateUserDataAndLogin(user, pass)
                    }
                  }
                  .build()
                  .show()
              }
              .build()
              .show()
        }
      }
    })

    viewModel.connectionProgress_liveData.observe(this, Observer { it ->
      val (state, progress, max) = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      when (state) {
        ConnectionState.CONNECTED,
        ConnectionState.DISCONNECTED,
        ConnectionState.CLOSED -> {
          progressBar.visibility = View.INVISIBLE
        }
        ConnectionState.INIT   -> {
          progressBar.visibility = View.VISIBLE
          // Show indeterminate when no progress has been made yet
          progressBar.isIndeterminate = progress == 0 || max == 0
          progressBar.progress = progress
          progressBar.max = max
        }
        else                   -> {
          progressBar.visibility = View.VISIBLE
          progressBar.isIndeterminate = true
        }
      }
    })

    editorPanel.addPanelSlideListener(panelSlideListener)
    editorPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
  }

  data class AutoCompletionState(
    val originalWord: String,
    val range: IntRange,
    val lastCompletion: AutoCompleteItem? = null,
    val completion: AutoCompleteItem
  )

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putInt("OPEN_BUFFER", viewModel.buffer.value ?: -1)
  }

  override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
    super.onSaveInstanceState(outState, outPersistentState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      outPersistentState?.putInt("OPEN_BUFFER", viewModel.buffer.value ?: -1)
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    viewModel.buffer.onNext(savedInstanceState?.getInt("OPEN_BUFFER", -1) ?: -1)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onRestoreInstanceState(savedInstanceState: Bundle?,
                                      persistentState: PersistableBundle?) {
    super.onRestoreInstanceState(savedInstanceState, persistentState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val fallback = persistentState?.getInt("OPEN_BUFFER", -1) ?: -1
      viewModel.buffer.onNext(savedInstanceState?.getInt("OPEN_BUFFER", fallback) ?: fallback)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home           -> {
      drawerToggle.onOptionsItemSelected(item)
    }

    R.id.action_filter_messages -> {
      runInBackground {
        viewModel.buffer { buffer ->
          val filtered = Message_Type.of(database.filtered().get(accountId, buffer) ?: 0)
          val flags = intArrayOf(
            Message.MessageType.Join.bit or Message.MessageType.NetsplitJoin.bit,
            Message.MessageType.Part.bit,
            Message.MessageType.Quit.bit or Message.MessageType.NetsplitQuit.bit,
            Message.MessageType.Nick.bit,
            Message.MessageType.Mode.bit,
            Message.MessageType.Topic.bit
          )
          val selectedIndices = flags.withIndex().mapNotNull { (index, flag) ->
            if ((filtered and flag).isNotEmpty()) {
              index
            } else {
              null
            }
          }.toTypedArray()

          MaterialDialog.Builder(this)
            .title(R.string.label_filter_messages)
            .items(R.array.message_filter_types)
            .itemsIds(flags)
            .itemsCallbackMultiChoice(selectedIndices, { _, _, _ -> false })
            .positiveText(R.string.label_select_multiple)
            .negativeText(R.string.label_cancel)
            .onPositive { dialog, _ ->
              val selected = dialog.selectedIndices ?: emptyArray()
              runInBackground {
                val newlyFiltered = selected
                  .map { flags[it] }
                  .fold(Message_Type.of()) { acc, i -> acc or i }

                database.filtered().replace(
                  QuasselDatabase.Filtered(accountId, buffer, newlyFiltered.value)
                )
              }
            }.negativeColorAttr(R.attr.colorTextPrimary)
            .backgroundColorAttr(R.attr.colorBackgroundCard)
            .contentColorAttr(R.attr.colorTextPrimary)
            .build()
            .show()
        }
      }
      true
    }
    R.id.action_settings        -> {
      startActivity(Intent(applicationContext, AppSettingsActivity::class.java))
      true
    }
    R.id.action_disconnect      -> {
      disconnect()
      true
    }
    else                        -> super.onOptionsItemSelected(item)
  }

  private fun disconnect() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }
}
