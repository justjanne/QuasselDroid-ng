/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.ui.setup

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.ActionMenuView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerAppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.nullIf
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutActivity
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashActivity
import de.kuschku.quasseldroid.ui.clientsettings.whitelist.WhitelistActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.service.BackendServiceConnection
import de.kuschku.quasseldroid.util.ui.LocaleHelper
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

abstract class ServiceBoundSetupActivity :
  DaggerAppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @BindView(R.id.menu_view)
  lateinit var menuView: ActionMenuView

  @BindView(R.id.view_pager)
  lateinit var viewPager: ViewPager

  @BindView(R.id.next_button)
  lateinit var button: FloatingActionButton

  private lateinit var adapter: SlidePagerAdapter

  protected abstract val fragments: List<SlideFragment>

  private val currentPage = MutableLiveData<SlideFragment?>()
  private val isValid = currentPage.switchMap(SlideFragment::valid).or(false)

  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend

  protected open val initData: Bundle = Bundle()

  protected fun runInBackground(f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager()?.handlerService?.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager()?.handlerService?.backendDelayed(delayMillis, f)
    }
  }

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var viewModel: QuasselViewModel

  protected var accountId: Long = -1

  class SetupActivityViewPagerPageChangeListener(private val activity: ServiceBoundSetupActivity) :
    ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
      when (state) {
        ViewPager.SCROLL_STATE_SETTLING -> activity.pageChanged()
      }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float,
                                positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) = Unit
  }

  private lateinit var pageChangeListener: SetupActivityViewPagerPageChangeListener

  private fun pageChanged() {
    val page = adapter.getItem(viewPager.currentItem)
    currentPage.value = page

    val finish = viewPager.currentItem == adapter.totalCount - 1
    button.contentDescription =
      if (finish) descriptionFinish
      else descriptionNext
    button.setTooltip()
    button.setImageDrawable(
      if (finish) drawableFinish
      else drawableNext
    )

    page.requestFocus()
  }

  fun updateRecentsHeader() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)
    }
  }

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  private var drawableFinish: Drawable? = null
  private var drawableNext: Drawable? = null

  private var descriptionFinish: String? = null
  private var descriptionNext: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_SetupTheme)
    super.onCreate(savedInstanceState)
    viewModel.backendWrapper.onNext(this.backend)
    packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
      .nullIf { it == 0 }?.let(this::setTitle)
    setContentView(R.layout.activity_setup)
    ButterKnife.bind(this)

    drawableFinish = getVectorDrawableCompat(R.drawable.ic_check)?.mutate()?.also {
      DrawableCompat.setTint(it, -1)
    }
    drawableNext = getVectorDrawableCompat(R.drawable.ic_arrow_right)?.mutate()?.also {
      DrawableCompat.setTint(it, -1)
    }

    descriptionFinish = getString(R.string.label_finish)
    descriptionNext = getString(R.string.label_next)

    menuView.popupTheme = R.style.Widget_PopupOverlay_Light
    menuInflater.inflate(R.menu.activity_setup, menuView.menu)
    menuView.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.action_client_settings -> {
          ClientSettingsActivity.launch(this)
          true
        }
        R.id.action_certificates    -> {
          WhitelistActivity.launch(this)
          true
        }
        R.id.action_crashes         -> {
          CrashActivity.launch(this)
          true
        }
        R.id.action_about           -> {
          AboutActivity.launch(this)
          true
        }
        else                        -> false
      }
    }

    adapter = SlidePagerAdapter(supportFragmentManager)
    fragments.forEach(adapter::addFragment)
    viewPager.adapter = adapter

    pageChangeListener = SetupActivityViewPagerPageChangeListener(this)

    button.setOnClickListener {
      if (viewPager.currentItem == adapter.totalCount - 1)
        onDoneInternal()
      else
        viewPager.setCurrentItem(viewPager.currentItem + 1, true)
    }
    isValid.observeSticky(
      this, Observer {
      if (it == true) {
        button.show()
        adapter.lastValidItem = viewPager.currentItem
      } else {
        button.hide()
        adapter.lastValidItem = viewPager.currentItem - 1
      }
    })
    viewPager.addOnPageChangeListener(pageChangeListener)
    pageChanged()
    updateRecentsHeader()
    adapter.result.putAll(initData)

    connection.context = this
    lifecycle.addObserver(connection)
    checkConnection()
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(LocaleHelper.setLocale(newBase))
  }

  private fun onDoneInternal() {
    onDone(adapter.result)
  }

  abstract fun onDone(data: Bundle)

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(currentItemKey, viewPager.currentItem)
    outState.putInt(lastValidItemKey, adapter.lastValidItem)
    outState.putBundle(resultKey, adapter.result)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(resultKey)) {
        adapter.result.putAll(savedInstanceState.getBundle(resultKey))
        adapter.allChanged()
      }
      if (savedInstanceState.containsKey(lastValidItemKey))
        adapter.lastValidItem = savedInstanceState.getInt(lastValidItemKey)
      if (savedInstanceState.containsKey(currentItemKey))
        viewPager.currentItem = savedInstanceState.getInt(currentItemKey)
      currentPage.value = adapter.getItem(viewPager.currentItem)
    }
    pageChanged()
  }

  override fun onStart() {
    if (Settings.appearance(this) != appearanceSettings) {
      recreate()
    }
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@ServiceBoundSetupActivity)
    }
    checkConnection()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@ServiceBoundSetupActivity)
    }
  }

  override fun onDestroy() {
    lifecycle.removeObserver(connection)
    super.onDestroy()
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) =
    checkConnection()

  private fun checkConnection() {
    accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
                  ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    val reconnect = sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      getBoolean(Keys.Status.reconnect, false)
    }
    val accountIdValid = accountId != -1L
    if (!reconnect || !accountIdValid) {
      finish()
    } else {
      connection.start()
      connection.bind()
    }
  }

  override fun onBackPressed() {
    if (viewPager.currentItem == 0)
      super.onBackPressed()
    else
      viewPager.currentItem -= 1
  }

  companion object {
    private const val currentItemKey = ":setupActivity:currentItem"
    private const val lastValidItemKey = ":setupActivity:lastValidItem"
    private const val resultKey = ":setupActivity:result"
  }
}
