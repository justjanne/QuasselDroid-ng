package de.kuschku.quasseldroid.ui.chat.info

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InfoFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindInfoFragment(): InfoFragment
}