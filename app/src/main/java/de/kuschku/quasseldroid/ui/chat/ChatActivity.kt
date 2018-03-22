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
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.util.and
import de.kuschku.libquassel.util.or
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.BacklogSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.chat.input.Editor
import de.kuschku.quasseldroid.ui.chat.input.MessageHistoryAdapter
import de.kuschku.quasseldroid.ui.settings.SettingsActivity
import de.kuschku.quasseldroid.ui.setup.accounts.AccountSelectionActivity
import de.kuschku.quasseldroid.util.AndroidHandlerThread
import de.kuschku.quasseldroid.util.helper.editApply
import de.kuschku.quasseldroid.util.helper.invoke
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.util.ui.MaterialContentLoadingProgressBar
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem

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

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var viewModel: QuasselViewModel

  private lateinit var database: QuasselDatabase

  private lateinit var backlogSettings: BacklogSettings

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
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    viewModel = ViewModelProviders.of(this)[QuasselViewModel::class.java]
    viewModel.setBackend(this.backend)
    backlogSettings = Settings.backlog(this)

    editor = Editor(
      this,
      viewModel.autoCompleteData,
      viewModel.lastWord,
      findViewById(R.id.chatline),
      findViewById(R.id.send),
      listOf(
        findViewById(R.id.autocomplete_list),
        findViewById(R.id.autocomplete_list_expanded)
      ),
      findViewById(R.id.formatting_menu),
      findViewById(R.id.formatting_toolbar),
      { lines ->
        viewModel.session { session ->
          viewModel.getBuffer().value?.let { bufferId ->
            session.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
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
    viewModel.recentlySentMessages.observe(this, Observer(messageHistoryAdapter::submitList))

    database = QuasselDatabase.Creator.init(application)

    setSupportActionBar(toolbar)

    viewModel.getBuffer().observe(
      this, Observer {
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

    viewModel.connectionProgress.observe(this, Observer { it ->
      val (state, progress, max) = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      when (state) {
        ConnectionState.CONNECTED, ConnectionState.DISCONNECTED -> {
          progressBar.hide()
        }
        ConnectionState.INIT                                    -> {
          // Show indeterminate when no progress has been made yet
          progressBar.isIndeterminate = progress == 0 || max == 0
          progressBar.progress = progress
          progressBar.max = max
        }
        else                                                    -> {
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
    outState?.putInt("OPEN_BUFFER", viewModel.getBuffer().value ?: -1)
  }

  override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
    super.onSaveInstanceState(outState, outPersistentState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      outPersistentState?.putInt("OPEN_BUFFER", viewModel.getBuffer().value ?: -1)
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    viewModel.setBuffer(savedInstanceState?.getInt("OPEN_BUFFER", -1) ?: -1)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onRestoreInstanceState(savedInstanceState: Bundle?,
                                      persistentState: PersistableBundle?) {
    super.onRestoreInstanceState(savedInstanceState, persistentState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val fallback = persistentState?.getInt("OPEN_BUFFER", -1) ?: -1
      viewModel.setBuffer(savedInstanceState?.getInt("OPEN_BUFFER", fallback) ?: fallback)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home -> {
      drawerToggle.onOptionsItemSelected(item)
    }

    R.id.filter_messages -> {
      handler.post {
        val buffer = viewModel.getBuffer().value
        if (buffer != null) {
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

          runOnUiThread {
            MaterialDialog.Builder(this)
              .title(R.string.label_filter_messages)
              .items(R.array.message_filter_types)
              .itemsIds(flags)
              .itemsCallbackMultiChoice(selectedIndices, { _, _, _ -> false })
              .positiveText(R.string.label_select_multiple)
              .negativeText(R.string.label_cancel)
              .onPositive { dialog, _ ->
                val selected = dialog.selectedIndices ?: emptyArray()
                handler.post {
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
      }
      true
    }
    R.id.settings -> {
      startActivity(Intent(applicationContext, SettingsActivity::class.java))
      true
    }
    R.id.disconnect -> {
      handler.post {
        sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
          editApply {
            putBoolean(Keys.Status.reconnect, false)
          }
        }

        val intent = Intent(this, AccountSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivityForResult(intent, REQUEST_SELECT_ACCOUNT)
      }
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}
