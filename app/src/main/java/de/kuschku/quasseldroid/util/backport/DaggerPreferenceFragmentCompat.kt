package de.kuschku.quasseldroid.util.backport

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class DaggerPreferenceFragmentCompat : PreferenceFragmentCompat(),
                                                HasSupportFragmentInjector {
  @Inject
  lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun onAttach(context: Context?) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
    return childFragmentInjector
  }
}
