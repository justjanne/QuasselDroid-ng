package de.kuschku.quasseldroid.ui.chat.info.channel

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChannelInfoFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindChannelInfoFragment(): ChannelInfoFragment
}
