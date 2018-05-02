/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
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
import android.view.*
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.session.Error
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.or
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteAdapter
import de.kuschku.quasseldroid.ui.chat.input.ChatlineFragment
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsActivity
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.util.ui.MaterialContentLoadingProgressBar
import de.kuschku.quasseldroid.viewmodel.data.BufferData
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.inject.Inject
import javax.inject.Named

class ChatActivity : ServiceBoundActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @BindView(R.id.drawer_layout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.progress_bar)
  lateinit var progressBar: MaterialContentLoadingProgressBar

  @BindView(R.id.editor_panel)
  lateinit var editorPanel: SlidingUpPanelLayout

  @BindView(R.id.autocomplete_list)
  lateinit var autoCompleteList: RecyclerView

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  @Named("ui")
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private var chatlineFragment: ChatlineFragment? = null

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    if (intent != null) {
      when {
        intent.type == "text/plain"            -> {
          chatlineFragment?.replaceText(intent.getStringExtra(Intent.EXTRA_TEXT))
          drawerLayout.closeDrawers()
        }
        intent.hasExtra(KEY_BUFFER_ID)         -> {
          viewModel.buffer.onNext(intent.getIntExtra(KEY_BUFFER_ID, -1))
          drawerLayout.closeDrawers()
        }
        intent.hasExtra(KEY_AUTOCOMPLETE_TEXT) -> {
          chatlineFragment?.editorHelper?.appendText(intent.getStringExtra(KEY_AUTOCOMPLETE_TEXT))
          drawerLayout.closeDrawers()
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    chatlineFragment = supportFragmentManager.findFragmentById(R.id.fragment_chatline) as? ChatlineFragment

    setSupportActionBar(toolbar)

    viewModel.buffer.toLiveData().observe(this, Observer {
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

    drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
      override fun onDrawerStateChanged(newState: Int) = Unit
      override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        actionMode?.finish()
      }

      override fun onDrawerClosed(drawerView: View) {
        actionMode?.finish()
      }

      override fun onDrawerOpened(drawerView: View) {
        actionMode?.finish()
      }
    })

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      chatlineFragment?.let {
        autoCompleteAdapter.setOnClickListener(it.chatline::autoComplete)
        autoCompleteList.layoutManager = LinearLayoutManager(it.activity)
        autoCompleteList.itemAnimator = DefaultItemAnimator()
        autoCompleteList.adapter = autoCompleteAdapter
        it.autoCompleteHelper.setDataListener {
          autoCompleteAdapter.submitList(it)
        }
      }
    }

    // If we connect to a new network without statusbuffer, the bufferid may be -networkId.
    // In that case, once we’re connected (and a status buffer exists), we want to switch to it.
    combineLatest(viewModel.allBuffers, viewModel.buffer).map { (buffers, current) ->
      if (current > 0) Optional.empty()
      else Optional.ofNullable(buffers.firstOrNull {
        it.networkId == -current && it.type.hasFlag(Buffer_Type.StatusBuffer)
      })
    }.toLiveData().observe(this, Observer { info ->
      info?.orNull()?.let {
        viewModel.buffer.onNext(it.bufferId)
      }
    })

    // User-actionable errors that require immediate action, and should show up as dialog
    viewModel.errors.toLiveData().observe(this, Observer { error ->
      error?.let {
        when (it) {
          is Error.HandshakeError -> it.message.let {
            when (it) {
              is HandshakeMessage.ClientInitReject  ->
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_init)
                  .content(Html.fromHtml(it.errorString))
                  .neutralText(R.string.label_close)
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
              is HandshakeMessage.CoreSetupReject   ->
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_setup)
                  .content(Html.fromHtml(it.errorString))
                  .neutralText(R.string.label_close)
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
              is HandshakeMessage.ClientLoginReject ->
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_login)
                  .content(Html.fromHtml(it.errorString))
                  .negativeText(R.string.label_disconnect)
                  .positiveText(R.string.label_update_user_password)
                  .onNegative { _, _ ->
                    disconnect()
                  }
                  .onPositive { _, _ ->
                    runInBackground {
                      val account = accountDatabase.accounts().findById(accountId)

                      runOnUiThread {
                        val dialog = MaterialDialog.Builder(this)
                          .title(R.string.label_error_login)
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
                          .titleColorAttr(R.attr.colorTextPrimary)
                          .backgroundColorAttr(R.attr.colorBackgroundCard)
                          .contentColorAttr(R.attr.colorTextPrimary)
                          .build()
                        dialog.customView?.run {
                          val userField = findViewById<EditText>(R.id.user)
                          val passField = findViewById<EditText>(R.id.pass)

                          account?.let {
                            userField.setText(it.user)
                          }
                        }
                        dialog.show()
                      }
                    }
                  }
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
            }
          }
          is Error.SslError       -> {
            it.exception.let {
              val leafCertificate = it.certificateChain?.firstOrNull()
              if (leafCertificate == null) {
                // No certificate exists in the chain
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_certificate)
                  .content(R.string.label_error_certificate_no_certificate)
                  .neutralText(R.string.label_close)
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
              } else {
                when {
                // Certificate has expired
                  it is QuasselSecurityException.Certificate &&
                  (it.cause is CertificateNotYetValidException ||
                   it.cause is CertificateExpiredException)  -> {
                    MaterialDialog.Builder(this)
                      .title(R.string.label_error_certificate)
                      .content(
                        Html.fromHtml(
                          getString(
                            R.string.label_error_certificate_invalid,
                            leafCertificate.fingerprint,
                            dateTimeFormatter.format(Instant.ofEpochMilli(leafCertificate.notBefore.time)
                                                       .atZone(ZoneId.systemDefault())),
                            dateTimeFormatter.format(Instant.ofEpochMilli(leafCertificate.notAfter.time)
                                                       .atZone(ZoneId.systemDefault()))
                          )
                        )
                      )
                      .negativeText(R.string.label_disconnect)
                      .positiveText(R.string.label_whitelist)
                      .onNegative { _, _ ->
                        disconnect()
                      }
                      .onPositive { _, _ ->
                        runInBackground {
                          database.validityWhitelist().save(
                            QuasselDatabase.SslValidityWhitelistEntry(
                              fingerprint = leafCertificate.fingerprint,
                              ignoreDate = true
                            )
                          )

                          runOnUiThread {
                            backend.value.orNull()?.reconnect()
                          }
                        }
                      }
                      .titleColorAttr(R.attr.colorTextPrimary)
                      .backgroundColorAttr(R.attr.colorBackgroundCard)
                      .contentColorAttr(R.attr.colorTextPrimary)
                      .build()
                      .show()
                  }
                // Certificate is in any other way invalid
                  it is QuasselSecurityException.Certificate -> {
                    MaterialDialog.Builder(this)
                      .title(R.string.label_error_certificate)
                      .content(
                        Html.fromHtml(
                          getString(
                            R.string.label_error_certificate_untrusted,
                            leafCertificate.fingerprint
                          )
                        )
                      )
                      .negativeText(R.string.label_disconnect)
                      .positiveText(R.string.label_whitelist)
                      .onNegative { _, _ ->
                        disconnect()
                      }
                      .onPositive { _, _ ->
                        runInBackground {
                          database.validityWhitelist().save(
                            QuasselDatabase.SslValidityWhitelistEntry(
                              fingerprint = leafCertificate.fingerprint,
                              ignoreDate = !leafCertificate.isValid
                            )
                          )
                          accountDatabase.accounts().findById(accountId)?.let {
                            database.hostnameWhitelist().save(
                              QuasselDatabase.SslHostnameWhitelistEntry(
                                fingerprint = leafCertificate.fingerprint,
                                hostname = it.host
                              )
                            )
                          }

                          runOnUiThread {
                            backend.value.orNull()?.reconnect()
                          }
                        }
                      }
                      .titleColorAttr(R.attr.colorTextPrimary)
                      .backgroundColorAttr(R.attr.colorBackgroundCard)
                      .contentColorAttr(R.attr.colorTextPrimary)
                      .build()
                      .show()
                  }
                // Certificate not valid for this hostname
                  it is QuasselSecurityException.Hostname    -> {
                    MaterialDialog.Builder(this)
                      .title(R.string.label_error_certificate)
                      .content(
                        Html.fromHtml(
                          getString(
                            R.string.label_error_certificate_no_match,
                            leafCertificate.fingerprint,
                            it.address.host
                          )
                        )
                      )
                      .negativeText(R.string.label_disconnect)
                      .positiveText(R.string.label_whitelist)
                      .onNegative { _, _ ->
                        disconnect()
                      }
                      .onPositive { _, _ ->
                        runInBackground {
                          database.hostnameWhitelist().save(
                            QuasselDatabase.SslHostnameWhitelistEntry(
                              fingerprint = leafCertificate.fingerprint,
                              hostname = it.address.host
                            )
                          )

                          runOnUiThread {
                            backend.value.orNull()?.reconnect()
                          }
                        }
                      }
                      .titleColorAttr(R.attr.colorTextPrimary)
                      .backgroundColorAttr(R.attr.colorBackgroundCard)
                      .contentColorAttr(R.attr.colorTextPrimary)
                      .build()
                      .show()
                  }
                }
              }
            }
          }
        }
      }
    })

    // After initial connect, open the drawer
    var isInitialConnect = true
    viewModel.connectionProgress
      .filter { (it, _, _) -> it == ConnectionState.CONNECTED }
      .firstElement()
      .toLiveData()
      .observe(this, Observer {
        if (resources.getBoolean(R.bool.buffer_drawer_exists) && viewModel.buffer.value == -1 && isInitialConnect) {
          drawerLayout.openDrawer(Gravity.START)
          isInitialConnect = true
        }
      })

    // Show Connection Progress Bar
    viewModel.connectionProgress.toLiveData().observe(this, Observer {
      val (state, progress, max) = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      when (state) {
        ConnectionState.CONNECTED -> {
          progressBar.visibility = View.INVISIBLE
        }
        ConnectionState.DISCONNECTED,
        ConnectionState.CLOSED    -> {
          progressBar.visibility = View.INVISIBLE
        }
        ConnectionState.INIT      -> {
          progressBar.visibility = View.VISIBLE
          // Show indeterminate when no progress has been made yet
          progressBar.isIndeterminate = progress == 0 || max == 0
          progressBar.progress = progress
          progressBar.max = max
        }
        else                      -> {
          progressBar.visibility = View.VISIBLE
          progressBar.isIndeterminate = true
        }
      }
    })

    // Only show nick list when we’re in a channel buffer
    viewModel.bufferData.distinctUntilChanged().toLiveData().observe(this, Observer {
      bufferData = it
      if (bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END)
      } else {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
      }

      invalidateOptionsMenu()
    })

    onNewIntent(intent)

    editorPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    chatlineFragment?.panelSlideListener?.let(editorPanel::addPanelSlideListener)
  }

  var bufferData: BufferData? = null
  var actionMode: ActionMode? = null

  override fun onActionModeStarted(mode: ActionMode?) {
    when (mode?.tag) {
      "BUFFER",
      "MESSAGES" -> mode.menu?.retint(toolbar.context)
    }
    actionMode = mode
    super.onActionModeStarted(mode)
  }

  override fun onActionModeFinished(mode: ActionMode?) {
    actionMode = null
    super.onActionModeFinished(mode)
  }

  override fun onStart() {
    if (Settings.autoComplete(this) != autoCompleteSettings) {
      recreate()
    }
    if (Settings.message(this) != messageSettings) {
      recreate()
    }
    super.onStart()
  }

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
    menu?.findItem(R.id.action_nicklist)?.isVisible = bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) ?: false
    menu?.findItem(R.id.action_filter_messages)?.isVisible = bufferData != null
    menu?.retint(toolbar.context)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home           -> {
      drawerToggle.onOptionsItemSelected(item)
    }
    R.id.action_nicklist        -> {
      if (drawerLayout.isDrawerVisible(Gravity.END)) {
        drawerLayout.closeDrawer(Gravity.END)
      } else {
        drawerLayout.openDrawer(Gravity.END)
      }
      true
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
            .positiveText(R.string.label_select)
            .negativeText(R.string.label_cancel)
            .onPositive { dialog, _ ->
              val selected = dialog.selectedIndices ?: emptyArray()
              runInBackground {
                val newlyFiltered = selected
                  .asSequence()
                  .map { flags[it] }
                  .fold(Message_Type.of()) { acc, i -> acc or i }

                database.filtered().replace(
                  QuasselDatabase.Filtered(accountId, buffer, newlyFiltered.value)
                )
              }
            }.negativeColorAttr(R.attr.colorTextPrimary)
            .backgroundColorAttr(R.attr.colorBackgroundCard)
            .contentColorAttr(R.attr.colorTextPrimary)
            .titleColorAttr(R.attr.colorTextPrimary)
            .build()
            .show()
        }
      }
      true
    }
    R.id.action_core_settings   -> {
      CoreSettingsActivity.launch(this)
      true
    }
    R.id.action_client_settings -> {
      ClientSettingsActivity.launch(this)
      true
    }
    R.id.action_disconnect      -> {
      disconnect()
      true
    }
    else                        -> super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    if (chatlineFragment?.historyPanel?.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
      chatlineFragment?.historyPanel?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
      return
    }

    if (editorPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
      editorPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
      return
    }

    super.onBackPressed()
  }

  private fun disconnect() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }

  companion object {
    private val KEY_AUTOCOMPLETE_TEXT = "autocomplete_text"
    private val KEY_BUFFER_ID = "buffer_id"

    fun launch(
      context: Context,
      sharedText: CharSequence? = null,
      autoCompleteText: CharSequence? = null,
      bufferId: Int? = null
    ) = context.startActivity(intent(context, sharedText, autoCompleteText, bufferId))

    fun intent(
      context: Context,
      sharedText: CharSequence? = null,
      autoCompleteText: CharSequence? = null,
      bufferId: Int? = null
    ) = Intent(context, ChatActivity::class.java).apply {
      if (sharedText != null) {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, sharedText)
      }
      if (autoCompleteText != null) {
        putExtra(KEY_AUTOCOMPLETE_TEXT, autoCompleteText)
      }
      if (bufferId != null) {
        putExtra(KEY_BUFFER_ID, bufferId)
      }
    }
  }
}
