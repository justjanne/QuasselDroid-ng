package de.kuschku.quasseldroid.util.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.helper.updateRecentsHeaderIfExisting
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

abstract class ServiceBoundActivity : AppCompatActivity(),
                                      SharedPreferences.OnSharedPreferenceChangeListener,
                                      HasSupportFragmentInjector,
                                      HasFragmentInjector {
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend


  @Inject
  lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

  @Inject
  lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
    return supportFragmentInjector
  }

  override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? {
    return frameworkFragmentInjector
  }

  protected fun runInBackground(f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backendDelayed(delayMillis, f)
    }
  }

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var connectionSettings: ConnectionSettings

  protected var accountId: Long = -1

  private var startedSelection = false

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    connection.context = this

    checkConnection()

    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
    updateRecentsHeader()
  }

  fun updateRecentsHeader() =
    updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  override fun onStart() {
    if (Settings.appearance(this) != appearanceSettings) {
      recreate()
    }
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
    connection.bind()
    checkConnection()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    connection.unbind()
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    checkConnection()
  }

  private fun checkConnection() {
    accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    val reconnect = sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      getBoolean(Keys.Status.reconnect, false)
    }
    val accountIdValid = accountId != -1L

    if (!reconnect || !accountIdValid) {

      if (!startedSelection) {
        startActivityForResult(
          Intent(this, AccountSelectionActivity::class.java), REQUEST_SELECT_ACCOUNT
        )
        startedSelection = true
      }
    } else {
      connection.start()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == REQUEST_SELECT_ACCOUNT) {
      startedSelection = false

      if (resultCode == Activity.RESULT_CANCELED) {
        finish()
      }
    }
  }

  protected fun stopService() {
    connection.unbind()
    connection.stop()
  }

  companion object {
    const val REQUEST_SELECT_ACCOUNT = 1
  }
}
