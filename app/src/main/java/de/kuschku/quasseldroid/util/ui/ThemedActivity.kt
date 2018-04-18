package de.kuschku.quasseldroid.util.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.kuschku.quasseldroid.settings.AppearanceSettings
import javax.inject.Inject

abstract class ThemedActivity : AppCompatActivity(), HasSupportFragmentInjector,
                                HasFragmentInjector {
  @Inject
  lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

  @Inject
  lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
  }

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
    return supportFragmentInjector
  }

  override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? {
    return frameworkFragmentInjector
  }
}
