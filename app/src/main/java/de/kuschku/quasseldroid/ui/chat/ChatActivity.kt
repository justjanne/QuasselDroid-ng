/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.system.ErrnoException
import android.text.Html
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.ProtocolVersionException
import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.coresetup.CoreSetupData
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_PLAINTEXT
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_SSL
import de.kuschku.libquassel.session.Error
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.or
import de.kuschku.libquassel.util.helper.*
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.ActivityMainBinding
import de.kuschku.quasseldroid.defaults.DefaultNetworkServer
import de.kuschku.quasseldroid.persistence.dao.*
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.models.Filtered
import de.kuschku.quasseldroid.persistence.models.SslHostnameWhitelistEntry
import de.kuschku.quasseldroid.persistence.models.SslValidityWhitelistEntry
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.NotificationSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteAdapter
import de.kuschku.quasseldroid.ui.chat.input.ChatlineFragment
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutActivity
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsActivity
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.ui.setup.core.CoreSetupActivity
import de.kuschku.quasseldroid.ui.setup.network.LinkNetwork
import de.kuschku.quasseldroid.ui.setup.network.NetworkSetupActivity
import de.kuschku.quasseldroid.ui.setup.user.UserSetupActivity
import de.kuschku.quasseldroid.util.backport.OsConstants
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.util.ui.DragInterceptBottomSheetBehavior
import de.kuschku.quasseldroid.util.ui.drawable.DrawerToggleActivityDrawable
import de.kuschku.quasseldroid.util.ui.drawable.NickCountDrawable
import de.kuschku.quasseldroid.util.ui.view.WarningBarView
import de.kuschku.quasseldroid.util.deceptive_networks.DeceptiveNetworkDialog
import de.kuschku.quasseldroid.viewmodel.ChatViewModel
import de.kuschku.quasseldroid.viewmodel.data.BufferData
import de.kuschku.quasseldroid.viewmodel.helper.ChatViewModelHelper
import io.reactivex.BackpressureStrategy
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.inject.Inject

@SuppressLint("ResourceType")
class ChatActivity : ServiceBoundActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  lateinit var binding: ActivityMainBinding

  @Inject
  lateinit var modelHelper: ChatViewModelHelper

  @Inject
  lateinit var chatViewModel: ChatViewModel

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var notificationSettings: NotificationSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  lateinit var editorBottomSheet: DragInterceptBottomSheetBehavior<View>

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  private lateinit var drawerToggle: ActionBarDrawerToggle

  private var chatlineFragment: ChatlineFragment? = null

  private var connectedAccount = AccountId(-1L)

  private var restoredDrawerState = false

  fun processIntent(intent: Intent) {
      when {
        intent.type == "text/plain"    -> {
          val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)
          if (text != null) {
            chatlineFragment?.replaceText(text)
            binding.drawerLayout.closeDrawers()
          }
        }
        intent.hasExtra(KEY_BUFFER_ID) -> {
          chatViewModel.bufferId.onNext(BufferId(intent.getIntExtra(KEY_BUFFER_ID, -1)))
          chatViewModel.bufferOpened.onNext(Unit)
          if (intent.hasExtra(KEY_ACCOUNT_ID)) {
            val accountId = AccountId(intent.getLongExtra(KEY_ACCOUNT_ID, -1L))
            if (accountId != this.accountId) {
              resetAccount()
              connectToAccount(accountId)
              startedSelection = false
              connectedAccount = AccountId(-1L)
              checkConnection()
              recreate()
            }
          }
        }
        intent.hasExtra(KEY_AUTOCOMPLETE_TEXT)                            -> {
          chatlineFragment?.editorHelper?.appendText(
            intent.getStringExtra(KEY_AUTOCOMPLETE_TEXT),
            intent.getStringExtra(KEY_AUTOCOMPLETE_SUFFIX)
          )
          binding.drawerLayout.closeDrawers()
        }
        intent.hasExtra(KEY_NETWORK_ID) && intent.hasExtra(KEY_CHANNEL)   -> {
          val networkId = NetworkId(intent.getIntExtra(KEY_NETWORK_ID, -1))
          val channel = intent.getStringExtra(KEY_CHANNEL)

          val forceJoin = intent.getBooleanExtra(KEY_FORCE_JOIN, false)

          modelHelper.connectedSession.filter(Optional<ISession>::isPresent).firstElement().subscribe {
            it.orNull()?.also { session ->
              val info = session.bufferSyncer.find(
                bufferName = channel,
                networkId = networkId,
                type = Buffer_Type.of(Buffer_Type.ChannelBuffer)
              )

              if (info != null && !forceJoin) {
                ChatActivity.launch(this, bufferId = info.bufferId)
              } else {
                modelHelper.chat.chatToJoin.onNext(Optional.of(
                  Pair(networkId, channel)
                ))

                session.bufferSyncer.find(
                  networkId = networkId,
                  type = Buffer_Type.of(Buffer_Type.StatusBuffer)
                )?.let { statusInfo ->
                  session.rpcHandler.sendInput(
                    statusInfo, "/join $channel"
                  )
                }
              }
            }
          }
        }
        intent.hasExtra(KEY_NETWORK_ID) && intent.hasExtra(KEY_NICK_NAME) -> {
          val networkId = NetworkId(intent.getIntExtra(KEY_NETWORK_ID, -1))
          val channel = intent.getStringExtra(KEY_NICK_NAME)

          val forceJoin = intent.getBooleanExtra(KEY_FORCE_JOIN, false)

          modelHelper.connectedSession.filter(Optional<ISession>::isPresent).firstElement().subscribe {
            it.orNull()?.also { session ->
              val info = session.bufferSyncer.find(
                bufferName = channel,
                networkId = networkId,
                type = Buffer_Type.of(Buffer_Type.QueryBuffer)
              )

              if (info != null && !forceJoin) {
                ChatActivity.launch(this, bufferId = info.bufferId)
              } else {
                modelHelper.allBuffers.map {
                  listOfNotNull(it.find {
                    it.networkId == networkId &&
                    it.bufferName == channel &&
                    it.type.hasFlag(Buffer_Type.QueryBuffer)
                  })
                }.filter {
                  it.isNotEmpty()
                }.firstElement().toLiveData().observeForever {
                  it?.firstOrNull()?.let { info ->
                    ChatActivity.launch(this, bufferId = info.bufferId)
                  }
                }

                session.bufferSyncer.find(
                  networkId = networkId,
                  type = Buffer_Type.of(Buffer_Type.StatusBuffer)
                )?.let { statusInfo ->
                  session.rpcHandler.sendInput(
                    statusInfo, "/query $channel"
                  )
                }
              }
            }
          }
        }
        intent.scheme == "irc" ||
        intent.scheme == "ircs"                                           -> {
          val uri = intent.data
          if (uri != null) {
            val channelString = (uri.path.let { it ?: "" }.trimStart('/')) +
                                (uri.fragment?.let { "#$it" }.let { it ?: "" })
            NetworkSetupActivity.launch(
              this,
              network = LinkNetwork(
                name = "",
                server = DefaultNetworkServer(
                  host = uri.host ?: "",
                  port = uri.port.nullIf { it < 0 }?.toUInt()
                         ?: if (uri.scheme == "irc") PORT_PLAINTEXT.port
                         else PORT_SSL.port,
                  secure = uri.scheme == "ircs"
                )
              ),
              channels = channelString.split(",").toTypedArray()
            )
          }
        }
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    chatlineFragment = supportFragmentManager.findFragmentById(R.id.fragment_chatline) as? ChatlineFragment

    setSupportActionBar(binding.layoutMain.layoutToolbar.toolbar)

    chatViewModel.bufferOpened.toLiveData().observe(this, Observer {
      actionMode?.finish()
      if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        binding.drawerLayout.closeDrawer(GravityCompat.START, true)
      }
    })

    // Don’t show a drawer toggle if in tablet landscape mode
    if (resources.getBoolean(R.bool.buffer_drawer_exists)) {
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
      drawerToggle = ActionBarDrawerToggle(
        this,
        binding.drawerLayout,
        R.string.label_open,
        R.string.label_close
      )
      drawerToggle.syncState()
    }

    binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
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

    val filtered = combineLatest(
      database.filtered().listenRx(accountId).toObservable().map {
        it.associateBy(Filtered::bufferId, Filtered::filtered)
      },
      accountDatabase.accounts().listenDefaultFiltered(accountId, 0).toObservable()
    )

    val maxBufferActivity = modelHelper.processBufferList(modelHelper.bufferViewConfig,
                                                          filtered).map { (config, bufferList) ->
      val minimumActivity: Buffer_Activity = config?.minimumActivity()?.enabledValues()?.max()
                                             ?: Buffer_Activity.NoActivity

      val maxActivity: Buffer_Activity = bufferList.mapNotNull {
        it.bufferActivity.enabledValues().max()
      }.max() ?: Buffer_Activity.NoActivity

      val hasNotifications = bufferList.any { props ->
        when {
          props.info.type hasFlag Buffer_Type.QueryBuffer   ->
            props.bufferActivity hasFlag Buffer_Activity.NewMessage
          props.info.type hasFlag Buffer_Type.ChannelBuffer ->
            props.highlights > 0
          else                                              -> false
        }
      }

      Pair(
        if (maxActivity < minimumActivity) Buffer_Activity.NoActivity
        else maxActivity,
        hasNotifications
      )
    }

    supportActionBar?.apply {
      val toggleDefault = DrawerToggleActivityDrawable(themedContext, 0)
      val toggleOtherActivity = DrawerToggleActivityDrawable(themedContext,
                                                             R.attr.colorTintActivity)
      val toggleNewMessage = DrawerToggleActivityDrawable(themedContext, R.attr.colorTintMessage)
      val toggleHighlight = DrawerToggleActivityDrawable(themedContext, R.attr.colorTintHighlight)
      val toggleNotification = DrawerToggleActivityDrawable(themedContext,
                                                            R.attr.colorTintNotification)
      maxBufferActivity.toLiveData()
        .observe(this@ChatActivity, Observer { (activity, hasNotifications) ->
          setHomeAsUpIndicator(
            when {
              notificationSettings.showAllActivitiesInToolbar &&
              activity == Buffer_Activity.Highlight     ->
                toggleHighlight
              notificationSettings.showAllActivitiesInToolbar &&
              activity == Buffer_Activity.NewMessage    ->
                toggleNewMessage
              notificationSettings.showAllActivitiesInToolbar &&
              activity == Buffer_Activity.OtherActivity ->
                toggleOtherActivity
              hasNotifications                          ->
                toggleNotification
              else                                      ->
                toggleDefault
            }
          )
        })
    }

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      val autoCompleteBottomSheet = BottomSheetBehavior.from(binding.layoutMain.autocompleteList)
      chatlineFragment?.let {
        autoCompleteAdapter.setOnClickListener(it.chatline::autoComplete)
        binding.layoutMain.autocompleteList.layoutManager = LinearLayoutManager(it.activity)
        binding.layoutMain.autocompleteList.itemAnimator = DefaultItemAnimator()
        binding.layoutMain.autocompleteList.adapter = autoCompleteAdapter
        it.autoCompleteHelper.addDataListener {
          autoCompleteBottomSheet.state =
            if (it.isEmpty()) BottomSheetBehavior.STATE_HIDDEN
            else BottomSheetBehavior.STATE_COLLAPSED
          autoCompleteAdapter.submitList(it)
        }
      }
    }
    // If we connect to a new network without statusbuffer, the bufferid may be -networkId.
    // In that case, once we’re connected (and a status bufferId exists), we want to switch to it.
    combineLatest(modelHelper.allBuffers, chatViewModel.bufferId).map { (buffers, current) ->
      if (current.isValidId()) Optional.empty()
      else Optional.ofNullable(buffers.firstOrNull {
        it.networkId == NetworkId(-current.id) && it.type.hasFlag(Buffer_Type.StatusBuffer)
      })
    }.toLiveData().observe(this, Observer { info ->
      info?.orNull()?.let {
        ChatActivity.launch(this, bufferId = it.bufferId)
      }
    })

    // User-actionable errors that require immediate action, and should show up as dialog
    modelHelper.errors.toLiveData(BackpressureStrategy.BUFFER).observe(this, Observer { error ->
      error?.let {
        when (it) {
          is Error.HandshakeError  -> it.message.let {
            when (it) {
              is HandshakeMessage.ClientInitAck     ->
                if (it.coreConfigured == false)
                  CoreSetupActivity.launch(
                    this,
                    accountDatabase.accounts().findById(accountId),
                    CoreSetupData.of(it)
                  )
              is HandshakeMessage.ClientInitReject  ->
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_init)
                  .content(Html.fromHtml(it.errorString))
                  .negativeText(R.string.label_disconnect)
                  .onNegative { _, _ ->
                    disconnect()
                  }
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
              is HandshakeMessage.CoreSetupReject   ->
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_setup)
                  .content(Html.fromHtml(it.errorString))
                  .negativeText(R.string.label_disconnect)
                  .onNegative { _, _ ->
                    disconnect()
                  }
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

                              backend.safeValue.orNull()?.updateUserDataAndLogin(user, pass)
                            }
                          }
                          .titleColorAttr(R.attr.colorTextPrimary)
                          .backgroundColorAttr(R.attr.colorBackgroundCard)
                          .contentColorAttr(R.attr.colorTextPrimary)
                          .build()
                        dialog.customView?.run {
                          val userField = findViewById<EditText>(R.id.user)

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
          is Error.SslError        -> {
            it.exception.let {
              if (it == QuasselSecurityException.NoSsl) {
                // Ssl is required but not available
                MaterialDialog.Builder(this)
                  .title(R.string.label_error_ssl)
                  .content(R.string.label_error_ssl_required_unavailable)
                  .neutralText(R.string.label_close)
                  .titleColorAttr(R.attr.colorTextPrimary)
                  .backgroundColorAttr(R.attr.colorBackgroundCard)
                  .contentColorAttr(R.attr.colorTextPrimary)
                  .build()
                  .show()
              } else {
                val leafCertificate = it.certificateChain?.firstOrNull()
                if (leafCertificate == null || it is QuasselSecurityException.NoCertificate) {
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
                              leafCertificate.sha1Fingerprint,
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
                              SslValidityWhitelistEntry(
                                fingerprint = leafCertificate.sha1Fingerprint,
                                ignoreDate = true
                              )
                            )

                            runOnUiThread {
                              log(INFO, "ChatActivity", "Reconnect triggered: User action")
                              backend.safeValue.orNull()?.autoConnect(ignoreErrors = true)
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
                    it is QuasselSecurityException.Certificate   -> {
                      MaterialDialog.Builder(this)
                        .title(R.string.label_error_certificate)
                        .content(
                          Html.fromHtml(
                            getString(
                              R.string.label_error_certificate_untrusted,
                              leafCertificate.sha1Fingerprint
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
                              SslValidityWhitelistEntry(
                                fingerprint = leafCertificate.sha1Fingerprint,
                                ignoreDate = !leafCertificate.isValid
                              )
                            )
                            accountDatabase.accounts().findById(accountId)?.let {
                              database.hostnameWhitelist().save(
                                SslHostnameWhitelistEntry(
                                  fingerprint = leafCertificate.sha1Fingerprint,
                                  hostname = it.host
                                )
                              )
                            }

                            runOnUiThread {
                              log(INFO, "ChatActivity", "Reconnect triggered: User action")
                              backend.safeValue.orNull()?.autoConnect(ignoreErrors = true)
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
                    it is QuasselSecurityException.WrongHostname -> {
                      MaterialDialog.Builder(this)
                        .title(R.string.label_error_certificate)
                        .content(
                          Html.fromHtml(
                            getString(
                              R.string.label_error_certificate_no_match,
                              leafCertificate.sha1Fingerprint,
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
                              SslHostnameWhitelistEntry(
                                fingerprint = leafCertificate.sha1Fingerprint,
                                hostname = it.address.host
                              )
                            )

                            runOnUiThread {
                              log(INFO, "ChatActivity", "Reconnect triggered: User action")
                              backend.safeValue.orNull()?.autoConnect(ignoreErrors = true)
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
          is Error.ConnectionError -> {
            it.throwable.let {
              val cause = it.cause
              when {
                it is UnknownHostException         -> {
                  val host = it.message?.replace("Host is unresolved: ", "")

                  Toast.makeText(this,
                                 getString(R.string.label_error_unknown_host, host),
                                 Toast.LENGTH_LONG).show()
                }
                it is ProtocolVersionException     -> {
                  val protocolVersion: Int = it.protocol.version.toInt()
                  Toast.makeText(this,
                                 getString(R.string.label_error_invalid_protocol_version,
                                           protocolVersion),
                                 Toast.LENGTH_LONG).show()
                }
                it is ConnectException &&
                cause is libcore.io.ErrnoException -> {
                  val errorCode = OsConstants.errnoName(cause.errno)
                  val errorName = OsConstants.strerror(cause.errno)

                  Toast.makeText(this,
                                 getString(R.string.label_error_connection, errorName, errorCode),
                                 Toast.LENGTH_LONG).show()
                }
                it is ConnectException &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                cause is ErrnoException            -> {
                  val errorCode = OsConstants.errnoName(cause.errno)
                  val errorName = OsConstants.strerror(cause.errno)

                  Toast.makeText(this,
                                 getString(R.string.label_error_connection, errorName, errorCode),
                                 Toast.LENGTH_LONG).show()
                }
                else                               -> {
                  Toast.makeText(this,
                                 getString(R.string.label_error_connection_closed),
                                 Toast.LENGTH_LONG).show()
                }
              }
            }
          }
        }
      }
    })

    // After initial connect, open the drawer
    modelHelper.connectionProgress
      .filter { (it, _, _) -> it == ConnectionState.CONNECTED }
      .firstElement()
      .toLiveData()
      .observe(this, Observer {
        if (connectedAccount != accountId) {
          if (resources.getBoolean(R.bool.buffer_drawer_exists) &&
              chatViewModel.bufferId.safeValue == BufferId.MAX_VALUE &&
              !restoredDrawerState) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
          }
          connectedAccount = accountId
          modelHelper.connectedSession.value?.orNull()?.let { session ->
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

    binding.layoutMain.connectionStatus.setOnClickListener {
      if (modelHelper.connectionProgress.value?.first == ConnectionState.CONNECTED
        && modelHelper.deceptiveNetwork.value == true) {
        DeceptiveNetworkDialog.Builder(this)
          .message(R.raw.untrustworthy_network_freenode)
          .show()
      } else {
        modelHelper.sessionManager.value?.orNull()?.apply {
          log(INFO, "ChatActivity", "Reconnect triggered: User action")
          backend.safeValue.orNull()?.autoConnect(ignoreErrors = true, ignoreSetting = true)
        }
      }
    }

    // Show Connection Progress Bar
    combineLatest(modelHelper.connectionProgress, modelHelper.deceptiveNetwork)
      .toLiveData().observe(this, Observer {
        val (connection, deceptive) = it ?: Pair(Triple(ConnectionState.DISCONNECTED, 0, 0), false)
        val (state, progress, max) = connection
        when (state) {
          ConnectionState.DISCONNECTED,
          ConnectionState.CLOSED     -> {
            binding.layoutMain.layoutToolbar.progressBar.visibility = View.INVISIBLE

            binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_ICON)
            binding.layoutMain.connectionStatus.setText(getString(R.string.label_status_disconnected))
          }
          ConnectionState.CONNECTING -> {
            binding.layoutMain.layoutToolbar.progressBar.visibility = View.VISIBLE
            binding.layoutMain.layoutToolbar.progressBar.isIndeterminate = true

            binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_PROGRESS)
            binding.layoutMain.connectionStatus.setText(getString(R.string.label_status_connecting))
          }
          ConnectionState.HANDSHAKE  -> {
            binding.layoutMain.layoutToolbar.progressBar.visibility = View.VISIBLE
            binding.layoutMain.layoutToolbar.progressBar.isIndeterminate = true

            binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_PROGRESS)
            binding.layoutMain.connectionStatus.setText(getString(R.string.label_status_handshake))
          }
          ConnectionState.INIT       -> {
            binding.layoutMain.layoutToolbar.progressBar.visibility = View.VISIBLE
            // Show indeterminate when no progress has been made yet
            binding.layoutMain.layoutToolbar.progressBar.isIndeterminate = progress == 0 || max == 0
            binding.layoutMain.layoutToolbar.progressBar.progress = progress
            binding.layoutMain.layoutToolbar.progressBar.max = max

            binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_PROGRESS)
            binding.layoutMain.connectionStatus.setText(getString(R.string.label_status_init))
          }
          ConnectionState.CONNECTED  -> {
            binding.layoutMain.layoutToolbar.progressBar.visibility = View.INVISIBLE
            if (deceptive) {
              binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_ICON)
              binding.layoutMain.connectionStatus.setText(R.string.deceptive_network)
            } else {
              binding.layoutMain.connectionStatus.setMode(WarningBarView.MODE_NONE)
            }
          }
        }
      })

    // Only show nick list when we’re in a channel bufferId
    modelHelper.bufferDataThrottled.toLiveData().observe(this, Observer {
      bufferData = it
      if (bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END)
      } else {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
      }

      invalidateOptionsMenu()
    })

    editorBottomSheet = DragInterceptBottomSheetBehavior.from(binding.root.findViewById(R.id.fragment_chatline))
    editorBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    chatlineFragment?.panelSlideListener?.let(editorBottomSheet::setBottomSheetCallback)

    chatlineFragment?.historyBottomSheet?.bottomSheetCallback = object :
      BottomSheetBehavior.BottomSheetCallback() {
      override fun onSlide(bottomSheet: View, slideOffset: Float) {
        val opacity = (1.0f - slideOffset) / 2.0f
        chatlineFragment?.editorContainer?.alpha = opacity
      }

      override fun onStateChanged(bottomSheet: View, newState: Int) {
        editorBottomSheet.allowDragging = newState == BottomSheetBehavior.STATE_HIDDEN
      }
    }

    combineLatest(modelHelper.allBuffers,
                  modelHelper.chat.chatToJoin).map { (buffers, chatToJoinOptional) ->
      val chatToJoin = chatToJoinOptional.orNull()
      if (chatToJoin == null) {
        emptyList()
      } else {
        val (networkId, channel) = chatToJoin

        listOfNotNull(buffers.find {
          it.networkId == networkId &&
          it.bufferName == channel &&
          it.type.hasFlag(Buffer_Type.ChannelBuffer)
        })
      }
    }.filter {
      it.isNotEmpty()
    }.firstElement().toLiveData().observeForever {
      it?.firstOrNull()?.let { info ->
        launch(this, bufferId = info.bufferId)
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  private var bufferData: BufferData? = null
  var actionMode: ActionMode? = null
  private var statusBarColor: Int? = null

  override fun onActionModeStarted(mode: ActionMode?) {
    when (mode?.tag) {
      "BUFFER",
      "MESSAGES" -> mode.menu?.retint(binding.layoutMain.layoutToolbar.toolbar.context)
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
    if (Settings.notification(this) != notificationSettings) {
      recreate()
    }
    super.onStart()
  }

  override fun onResume() {
    super.onResume()
    intent?.let(this@ChatActivity::processIntent)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    chatViewModel.onSaveInstanceState(outState)

    outState.putLong(KEY_CONNECTED_ACCOUNT, connectedAccount.id)
    outState.putBoolean(KEY_OPEN_DRAWER_START, binding.drawerLayout.isDrawerOpen(GravityCompat.START))
    outState.putBoolean(KEY_OPEN_DRAWER_END, binding.drawerLayout.isDrawerOpen(GravityCompat.END))
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    if (savedInstanceState != null) {
      chatViewModel.onRestoreInstanceState(savedInstanceState)
    }

    connectedAccount = AccountId(savedInstanceState?.getLong(KEY_CONNECTED_ACCOUNT, -1L) ?: -1L)

    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_START) == true &&
        resources.getBoolean(R.bool.buffer_drawer_exists)) {
      binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_END) == true) {
      binding.drawerLayout.openDrawer(GravityCompat.END)
    }
    if (savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_START) != null ||
        savedInstanceState?.getBoolean(KEY_OPEN_DRAWER_END) != null) {
      restoredDrawerState = true
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val nickCountDrawableSize = resources.getDimensionPixelSize(R.dimen.size_nick_count)
    val nickCountDrawableColor = binding.layoutMain.layoutToolbar.toolbar.context.theme.styledAttributes(R.attr.colorControlNormal) {
      getColor(0, 0)
    }

    menuInflater.inflate(R.menu.activity_main, menu)
    menu?.findItem(R.id.action_nicklist)?.isVisible = bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer)
                                                      ?: false
    menu?.findItem(R.id.action_filter_messages)?.isVisible =
      (bufferData?.info?.type?.hasFlag(Buffer_Type.ChannelBuffer) ?: false ||
       bufferData?.info?.type?.hasFlag(Buffer_Type.QueryBuffer) ?: false)
    menu?.retint(binding.layoutMain.layoutToolbar.toolbar.context)
    menu?.findItem(R.id.action_nicklist)?.icon = NickCountDrawable(
      bufferData?.userCount ?: 0,
      nickCountDrawableSize,
      nickCountDrawableColor)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home           -> {
      drawerToggle.onOptionsItemSelected(item)
    }
    R.id.action_nicklist        -> {
      if (binding.drawerLayout.isDrawerVisible(GravityCompat.END)) {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
      } else {
        binding.drawerLayout.openDrawer(GravityCompat.END)
      }
      true
    }
    R.id.action_filter_messages -> {
      runInBackground {
        chatViewModel.bufferId { buffer ->
          val filteredRaw = database.filtered().get(
            accountId,
            buffer,
            accountDatabase.accounts().findById(accountId)?.defaultFiltered ?: 0
          )
          val filtered = Message_Type.of(filteredRaw)
          val flags = uintArrayOf(
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
            .itemsIds(flags.toIntArray())
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

                accountDatabase.accounts().setFiltered(accountId, newlyFiltered.value.toInt())
                database.filtered().setFiltered(accountId, buffer, newlyFiltered.value.toInt())
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
                  Filtered.of(accountId, buffer, newlyFiltered.value.toInt())
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
    R.id.action_core_settings   -> {
      CoreSettingsActivity.launch(this)
      true
    }
    R.id.action_client_settings -> {
      ClientSettingsActivity.launch(this)
      true
    }
    R.id.action_about           -> {
      AboutActivity.launch(this)
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
      connectedAccount = AccountId(-1L)

      if (resultCode == Activity.RESULT_CANCELED) {
        finish()
      }
    }
  }

  private fun resetAccount() {
    startedSelection = true
    connectedAccount = AccountId(-1L)
    restoredDrawerState = false
    ChatActivity.launch(this, bufferId = BufferId.MAX_VALUE)
    chatViewModel.resetAccount()
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
    private const val KEY_NICK_NAME = "nick_name"
    private const val KEY_FORCE_JOIN = "force_join"

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
      nickName: String? = null,
      networkId: NetworkId? = null,
      bufferId: BufferId? = null,
      accountId: Long? = null,
      forceJoin: Boolean? = null
    ) = context.startActivity(
      intent(context,
             sharedText,
             autoCompleteText,
             autoCompleteSuffix,
             channel,
             nickName,
             networkId,
             bufferId,
             accountId,
             forceJoin)
    )

    fun intent(
      context: Context,
      sharedText: CharSequence? = null,
      autoCompleteText: CharSequence? = null,
      autoCompleteSuffix: String? = null,
      channel: String? = null,
      nickName: String? = null,
      networkId: NetworkId? = null,
      bufferId: BufferId? = null,
      accountId: Long? = null,
      forceJoin: Boolean? = null
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
        putExtra(KEY_BUFFER_ID, bufferId.id)
        if (accountId != null) {
          putExtra(KEY_ACCOUNT_ID, accountId)
        }
      }
      if (networkId != null && channel != null) {
        putExtra(KEY_NETWORK_ID, networkId.id)
        putExtra(KEY_CHANNEL, channel)
      } else if (networkId != null && nickName != null) {
        putExtra(KEY_NETWORK_ID, networkId.id)
        putExtra(KEY_NICK_NAME, nickName)
        if (forceJoin != null) {
          putExtra(KEY_NICK_NAME, nickName)
        }
      }
    }
  }
}
