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

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
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
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.session.Error
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.or
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteAdapter
import de.kuschku.quasseldroid.ui.chat.input.ChatlineFragment
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsActivity
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.ui.setup.user.UserSetupActivity
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.util.ui.DragInterceptBottomSheetBehavior
import de.kuschku.quasseldroid.util.ui.MaterialContentLoadingProgressBar
import de.kuschku.quasseldroid.util.ui.NickCountDrawable
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.data.BufferData
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.inject.Inject

class ChatActivity : ServiceBoundActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @BindView(R.id.drawer_layout)
  lateinit var drawerLayout: DrawerLayout

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.progress_bar)
  lateinit var progressBar: MaterialContentLoadingProgressBar

  @BindView(R.id.autocomplete_list)
  lateinit var autoCompleteList: RecyclerView

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  lateinit var editorBottomSheet: DragInterceptBottomSheetBehavior<View>

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private var chatlineFragment: ChatlineFragment? = null

  private var connectedAccount = -1L

  private var restoredDrawerState = false

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    if (intent != null) {
      when {
        intent.type == "text/plain"                                     -> {
          chatlineFragment?.replaceText(intent.getStringExtra(Intent.EXTRA_TEXT))
          drawerLayout.closeDrawers()
        }
        intent.hasExtra(KEY_BUFFER_ID)                                  -> {
          viewModel.buffer.onNext(intent.getIntExtra(KEY_BUFFER_ID, -1))
          viewModel.bufferOpened.onNext(Unit)
          if (intent.hasExtra(KEY_ACCOUNT_ID)) {
            val accountId = intent.getLongExtra(ChatActivity.KEY_ACCOUNT_ID, -1)
            if (accountId != this.accountId) {
              resetAccount()
              connectToAccount(accountId)
              startedSelection = false
              connectedAccount = -1L
              checkConnection()
              recreate()
            }
          }
        }
        intent.hasExtra(KEY_AUTOCOMPLETE_TEXT)                          -> {
          chatlineFragment?.editorHelper?.appendText(
            intent.getStringExtra(KEY_AUTOCOMPLETE_TEXT),
            intent.getStringExtra(KEY_AUTOCOMPLETE_SUFFIX)
          )
          drawerLayout.closeDrawers()
        }
        intent.hasExtra(KEY_NETWORK_ID) && intent.hasExtra(KEY_CHANNEL) -> {
          val networkId = intent.getIntExtra(KEY_NETWORK_ID, -1)
          val channel = intent.getStringExtra(KEY_CHANNEL)

          viewModel.session.value?.orNull()?.also { session ->
            val info = session.bufferSyncer?.find(
              bufferName = channel,
              networkId = networkId,
              type = Buffer_Type.of(Buffer_Type.ChannelBuffer)
            )

            if (info != null) {
              viewModel.buffer.onNext(info.bufferId)
              viewModel.bufferOpened.onNext(Unit)
            } else {
              viewModel.allBuffers.map {
                listOfNotNull(it.find {
                  it.networkId == networkId &&
                  it.bufferName == channel &&
                  it.type.hasFlag(Buffer_Type.ChannelBuffer)
                })
              }.filter {
                it.isNotEmpty()
              }.firstElement().toLiveData().observe(this, Observer {
                it?.firstOrNull()?.let { info ->
                  viewModel.buffer.onNext(info.bufferId)
                  viewModel.bufferOpened.onNext(Unit)
                }
              })

              session.bufferSyncer?.find(
                networkId = networkId,
                type = Buffer_Type.of(Buffer_Type.StatusBuffer)
              )?.let { statusInfo ->
                session.rpcHandler?.sendInput(
                  statusInfo, "/join $channel"
                )
              }
            }
          }
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

    viewModel.bufferOpened.toLiveData().observe(this, Observer {
      actionMode?.finish()
      if (drawerLayout.isDrawerOpen(Gravity.START)) {
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
      val autoCompleteBottomSheet = BottomSheetBehavior.from(autoCompleteList)
      chatlineFragment?.let {
        autoCompleteAdapter.setOnClickListener(it.chatline::autoComplete)
        autoCompleteList.layoutManager = LinearLayoutManager(it.activity)
        autoCompleteList.itemAnimator = DefaultItemAnimator()
        autoCompleteList.adapter = autoCompleteAdapter
        it.autoCompleteHelper.addDataListener {
          autoCompleteBottomSheet.state =
            if (it.isEmpty()) BottomSheetBehavior.STATE_HIDDEN
            else BottomSheetBehavior.STATE_COLLAPSED
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
    viewModel.connectionProgress
      .filter { (it, _, _) -> it == ConnectionState.CONNECTED }
      .firstElement()
      .toLiveData()
      .observe(this, Observer {
        if (connectedAccount != accountId) {
          if (resources.getBoolean(R.bool.buffer_drawer_exists) &&
              viewModel.buffer.value == Int.MAX_VALUE &&
              !restoredDrawerState) {
            drawerLayout.openDrawer(Gravity.START)
          }
          connectedAccount = accountId
          viewModel.session.value?.orNull()?.let { session ->
            if (session.identities.isEmpty()) {
              UserSetupActivity.launch(this)
            }
            val missingFeatures = RequiredFeatures.features.filter {
              it.feature !in session.features.core.enabledFeatures
            }
            if (missingFeatures.isNotEmpty()) {
              runInBackground {
                val accounts = accountDatabase.accounts()
                val account = accounts.findById(accountId)
                if (account?.acceptedMissingFeatures == false) {
                  val dialog = MissingFeaturesDialog.Builder(this)
                    .missingFeatures(missingFeatures)
                    .positiveListener(MaterialDialog.SingleButtonCallback { _, _ ->
                      runInBackground {
                        accounts.save(account.copy(acceptedMissingFeatures = true))
                      }
                    })
                  runOnUiThread {
                    dialog.show()
                  }
                }
              }
            }
          }
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

    editorBottomSheet = DragInterceptBottomSheetBehavior.from(chatlineFragment?.view)
    editorBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    chatlineFragment?.panelSlideListener?.let(editorBottomSheet::setBottomSheetCallback)

    chatlineFragment?.historyBottomSheet?.setBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          val opacity = (1.0f - slideOffset) / 2.0f
          chatlineFragment?.editorContainer?.alpha = opacity
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
          editorBottomSheet.allowDragging = newState == BottomSheetBehavior.STATE_HIDDEN
        }
      }
    )

    onNewIntent(intent)
  }

  var bufferData: BufferData? = null
  var actionMode: ActionMode? = null
  private var statusBarColor: Int? = null

  override fun onActionModeStarted(mode: ActionMode?) {
    when (mode?.tag) {
      "BUFFER",
      "MESSAGES" -> mode.menu?.retint(toolbar.context)
    }
    actionMode = mode
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      statusBarColor = window.statusBarColor
      window.statusBarColor = theme.styledAttributes(R.attr.colorPrimaryDark) {
        getColor(0, 0)
      }
    }
    super.onActionModeStarted(mode)
  }

  override fun onActionModeFinished(mode: ActionMode?) {
    actionMode = null
    super.onActionModeFinished(mode)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      statusBarColor?.let {
        window.statusBarColor = it
        statusBarColor = null
      }
    }
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
    outState?.putInt(KEY_OPEN_BUFFER, viewModel.buffer.value ?: -1)
    outState?.putInt(KEY_OPEN_BUFFERVIEWCONFIG, viewModel.bufferViewConfigId.value ?: -1)
    outState?.putLong(KEY_CONNECTED_ACCOUNT, connectedAccount)
    outState?.putBoolean(KEY_OPEN_DRAWER_START, drawerLayout.isDrawerOpen(Gravity.START))
    outState?.putBoolean(KEY_OPEN_DRAWER_END, drawerLayout.isDrawerOpen(Gravity.END))
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    viewModel.buffer.onNext(savedInstanceState?.getInt(KEY_OPEN_BUFFER, -1) ?: -1)
    viewModel.bufferViewConfigId.onNext(savedInstanceState?.getInt(KEY_OPEN_BUFFERVIEWCONFIG, -1)
                                        ?: -1)
    connectedAccount = savedInstanceState?.getLong(KEY_CONNECTED_ACCOUNT, -1L) ?: -1L

    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_START) == true) {
      drawerLayout.openDrawer(Gravity.START)
    }
    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_END) == true) {
      drawerLayout.openDrawer(Gravity.END)
    }
    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_START) != null ||
        savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_END) != null) {
      restoredDrawerState = true
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val nickCountDrawableSize = resources.getDimensionPixelSize(R.dimen.size_nick_count)
    val nickCountDrawableColor = toolbar.context.theme.styledAttributes(R.attr.colorControlNormal) {
      getColor(0, 0)
    }

    menuInflater.inflate(R.menu.activity_main, menu)
    menu?.findItem(R.id.action_nicklist)?.isVisible = bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) ?: false
    menu?.findItem(R.id.action_filter_messages)?.isVisible =
      (bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) ?: false ||
       bufferData?.info?.type?.hasFlag(Buffer_Type.QueryBuffer) ?: false)
    menu?.findItem(R.id.action_create_shortcut)?.isVisible =
      (bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) ?: false ||
       bufferData?.info?.type?.hasFlag(Buffer_Type.QueryBuffer) ?: false) &&
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    menu?.retint(toolbar.context)
    viewModel.nickData.toLiveData().observe(this, Observer {
      val count = it?.count() ?: 0
      menu?.findItem(R.id.action_nicklist)?.icon = NickCountDrawable(count,
                                                                     nickCountDrawableSize,
                                                                     nickCountDrawableColor)
    })
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
          val filteredRaw = database.filtered().get(accountId,
                                                    buffer,
                                                    accountDatabase.accounts().findById(accountId)?.defaultFiltered
                                                    ?: 0)
          val filtered = Message_Type.of(filteredRaw)
          val flags = intArrayOf(
            Message.MessageType.Join.bit or Message.MessageType.NetsplitJoin.bit,
            Message.MessageType.Part.bit,
            Message.MessageType.Quit.bit or Message.MessageType.NetsplitQuit.bit,
            Message.MessageType.Nick.bit,
            Message.MessageType.Mode.bit,
            Message.MessageType.Topic.bit
          )
          val selectedIndices = flags.withIndex().mapNotNull { (index, flag) ->
            if ((filtered and flag).isNotEmpty()) index
            else null
          }.toTypedArray()

          MaterialDialog.Builder(this)
            .title(R.string.label_filter_messages)
            .items(R.array.message_filter_types)
            .itemsIds(flags)
            .itemsCallbackMultiChoice(selectedIndices) { _, _, _ -> false }
            .positiveText(R.string.label_select)
            .negativeText(R.string.label_use_default)
            .onNegative { _, _ ->
              runInBackground {
                database.filtered().clear(accountId, buffer)
              }
            }
            .neutralText(R.string.label_set_default)
            .onNeutral { dialog, _ ->
              val selected = dialog.selectedIndices ?: emptyArray()
              runInBackground {
                val newlyFiltered = selected
                  .asSequence()
                  .map { flags[it] }
                  .fold(Message_Type.of()) { acc, i -> acc or i }

                accountDatabase.accounts().setFiltered(accountId, newlyFiltered.value)
                database.filtered().setFiltered(accountId, buffer, newlyFiltered.value)
              }
            }
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
            }
            .negativeColorAttr(R.attr.colorTextPrimary)
            .neutralColorAttr(R.attr.colorTextPrimary)
            .backgroundColorAttr(R.attr.colorBackgroundCard)
            .contentColorAttr(R.attr.colorTextPrimary)
            .titleColorAttr(R.attr.colorTextPrimary)
            .build()
            .show()
        }
      }
      true
    }
    R.id.action_create_shortcut -> {
      bufferData?.also { data ->
        data.info?.also { info ->
          val callback: (IconCompat) -> Unit = { icon ->
            ShortcutManagerCompat.requestPinShortcut(
              this,
              ShortcutInfoCompat.Builder(this, "${System.currentTimeMillis()}")
                .setShortLabel(info.bufferName ?: "")
                .setIcon(icon)
                .setIntent(
                  ChatActivity.intent(
                    this,
                    bufferId = info.bufferId,
                    accountId = accountId
                  ).setAction(Intent.ACTION_VIEW)
                )
                .build(),
              null
            )
          }

          val resultAvailable: (Drawable) -> Unit = { resource ->
            val bitmap = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            resource.setBounds(0, 0, canvas.width, canvas.height)
            resource.draw(canvas)
            callback(IconCompat.createWithAdaptiveBitmap(bitmap))
          }

          val senderColors = theme.styledAttributes(
            R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
            R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
            R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
            R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
          ) {
            IntArray(length()) {
              getColor(it, 0)
            }
          }

          val colorContext = ColorContext(this, messageSettings)

          if (info.type.hasFlag(Buffer_Type.QueryBuffer)) {
            val nickName = info.bufferName ?: ""
            val senderColorIndex = SenderColorUtil.senderColor(nickName)
            val rawInitial = nickName.trimStart(*EditorViewModel.IGNORED_CHARS).firstOrNull()
                             ?: nickName.firstOrNull()
            val initial = rawInitial?.toUpperCase().toString()
            val senderColor = senderColors[senderColorIndex]

            val fallback = colorContext.prepareTextDrawable()
              .beginConfig()
              .scale(0.5f)
              .endConfig()
              .buildRect(initial, senderColor)

            val urls = viewModel.networks.value?.get(info.networkId)?.ircUser(info.bufferName)?.let {
              AvatarHelper.avatar(messageSettings, it, 432)
            }

            if (urls == null || urls.isEmpty()) {
              resultAvailable(fallback)
            } else {
              GlideApp.with(this)
                .loadWithFallbacks(urls)
                ?.placeholder(fallback)
                ?.into(object : SimpleTarget<Drawable>(432, 432) {
                  override fun onResourceReady(resource: Drawable,
                                               transition: Transition<in Drawable>?) {
                    resultAvailable(resource)
                  }

                  override fun onLoadFailed(errorDrawable: Drawable?) {
                    resultAvailable(errorDrawable!!)
                  }
                })
            }
          } else {
            callback(IconCompat.createWithResource(this, R.drawable.ic_shortcut_channel))
          }
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
    if (chatlineFragment?.historyBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
      chatlineFragment?.historyBottomSheet?.state = BottomSheetBehavior.STATE_HIDDEN
      return
    }

    if (editorBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
      editorBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
      return
    }

    super.onBackPressed()
  }

  private fun disconnect() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }

  private var startedSelection = false

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == REQUEST_SELECT_ACCOUNT) {
      startedSelection = false
      connectedAccount = -1L

      if (resultCode == Activity.RESULT_CANCELED) {
        finish()
      }
    }
  }

  private fun resetAccount() {
    startedSelection = true
    connectedAccount = -1L
    restoredDrawerState = false
    viewModel.resetAccount()
  }

  override fun onSelectAccount() {
    if (!startedSelection) {
      resetAccount()
      startActivityForResult(AccountSelectionActivity.intent(this), REQUEST_SELECT_ACCOUNT)
    }
  }

  companion object {
    // Intent keys
    private const val KEY_AUTOCOMPLETE_TEXT = "autocomplete_text"
    private const val KEY_AUTOCOMPLETE_SUFFIX = "autocomplete_suffix"
    private const val KEY_BUFFER_ID = "buffer_id"
    private const val KEY_ACCOUNT_ID = "account_id"
    private const val KEY_NETWORK_ID = "network_id"
    private const val KEY_CHANNEL = "channel"

    // Instance state keys
    private const val KEY_OPEN_BUFFER = "open_buffer"
    private const val KEY_OPEN_BUFFERVIEWCONFIG = "open_bufferviewconfig"
    private const val KEY_CONNECTED_ACCOUNT = "connected_account"
    private const val KEY_OPEN_DRAWER_START = "open_drawer_start"
    private const val KEY_OPEN_DRAWER_END = "open_drawer_end"

    fun launch(
      context: Context,
      sharedText: CharSequence? = null,
      autoCompleteText: CharSequence? = null,
      autoCompleteSuffix: String? = null,
      channel: String? = null,
      networkId: NetworkId? = null,
      bufferId: Int? = null,
      accountId: Long? = null
    ) = context.startActivity(
      intent(context,
             sharedText,
             autoCompleteText,
             autoCompleteSuffix,
             channel,
             networkId,
             bufferId,
             accountId)
    )

    fun intent(
      context: Context,
      sharedText: CharSequence? = null,
      autoCompleteText: CharSequence? = null,
      autoCompleteSuffix: String? = null,
      channel: String? = null,
      networkId: NetworkId? = null,
      bufferId: Int? = null,
      accountId: Long? = null
    ) = Intent(context, ChatActivity::class.java).apply {
      if (sharedText != null) {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, sharedText)
      }
      if (autoCompleteText != null) {
        putExtra(KEY_AUTOCOMPLETE_TEXT, autoCompleteText)
        if (autoCompleteSuffix != null) {
          putExtra(KEY_AUTOCOMPLETE_SUFFIX, autoCompleteSuffix)
        }
      }
      if (bufferId != null) {
        putExtra(KEY_BUFFER_ID, bufferId)
        if (accountId != null) {
          putExtra(KEY_ACCOUNT_ID, accountId)
        }
      }
      if (networkId != null && channel != null) {
        putExtra(KEY_NETWORK_ID, networkId)
        putExtra(KEY_CHANNEL, channel)
      }
    }
  }
}
