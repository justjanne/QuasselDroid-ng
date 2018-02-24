package de.kuschku.quasseldroid_ng.ui.chat

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.util.and
import de.kuschku.libquassel.util.or
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.SettingsActivity
import de.kuschku.quasseldroid_ng.ui.settings.data.BacklogSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.Settings
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.editApply
import de.kuschku.quasseldroid_ng.util.helper.invoke
import de.kuschku.quasseldroid_ng.util.helper.let
import de.kuschku.quasseldroid_ng.util.helper.sharedPreferences
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid_ng.util.ui.MaterialContentLoadingProgressBar

class ChatActivity : ServiceBoundActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @BindView(R.id.drawerLayout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.progressBar)
  lateinit var progressBar: MaterialContentLoadingProgressBar

  @BindView(R.id.editor_panel)
  lateinit var editorPanel: SlidingUpPanelLayout

  @BindView(R.id.send)
  lateinit var send: ImageButton

  @BindView(R.id.chatline)
  lateinit var chatline: EditText

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var viewModel: QuasselViewModel

  private var snackbar: Snackbar? = null

  private lateinit var database: QuasselDatabase

  private lateinit var backlogSettings: BacklogSettings

  private val panelSlideListener: SlidingUpPanelLayout.PanelSlideListener = object :
    SlidingUpPanelLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View?, slideOffset: Float) = Unit

    override fun onPanelStateChanged(panel: View?,
                                     previousState: SlidingUpPanelLayout.PanelState?,
                                     newState: SlidingUpPanelLayout.PanelState?) {
      val selectionStart = chatline.selectionStart
      val selectionEnd = chatline.selectionEnd

      when (newState) {
        SlidingUpPanelLayout.PanelState.COLLAPSED ->
          chatline.inputType = chatline.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv()
        else                                      ->
          chatline.inputType = chatline.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
      }

      chatline.setSelection(selectionStart, selectionEnd)
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

    database = QuasselDatabase.Creator.init(application)

    setSupportActionBar(toolbar)

    send.setOnClickListener {
      send()
    }

    chatline.setOnKeyListener { _, keyCode, event ->
      if (event.hasNoModifiers() && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
        send()
        true
      } else {
        false
      }
    }

    viewModel.getBuffer().observe(
      this, Observer {
      if (it != null && drawerLayout.isDrawerOpen(Gravity.START)) {
        drawerLayout.closeDrawer(Gravity.START, true)
      }
    }
    )

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    drawerToggle = ActionBarDrawerToggle(
      this,
      drawerLayout,
      R.string.label_open,
      R.string.label_close
    )
    drawerToggle.syncState()

    viewModel.connectionProgress.observe(this, Observer { it ->
      val (state, progress, max) = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      when (state) {
        ConnectionState.CONNECTED, ConnectionState.DISCONNECTED -> {
          progressBar.hide()
        }
        ConnectionState.INIT                                    -> {
          progressBar.isIndeterminate = true
        }
        else                                                    -> {
          progressBar.isIndeterminate = false
          progressBar.progress = progress
          progressBar.max = max
        }
      }
    })

    editorPanel.addPanelSlideListener(panelSlideListener)
    editorPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
  }

  private fun send() {
    viewModel.session { session ->
      viewModel.getBuffer().let { bufferId ->
        session.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
          session.rpcHandler?.sendInput(bufferInfo, chatline.text.toString())
        }
      }
    }
    chatline.text.clear()
  }

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
    android.R.id.home    -> {
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
    R.id.clear           -> {
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
    R.id.settings        -> {
      startActivity(Intent(applicationContext, SettingsActivity::class.java))
      true
    }
    R.id.disconnect      -> {
      handler.post {
        sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
          editApply {
            putBoolean(Keys.Status.reconnect, false)
          }
        }
        setResult(Activity.RESULT_OK)
        finish()
      }
      true
    }
    else                 -> super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}
