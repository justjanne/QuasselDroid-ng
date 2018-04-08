package de.kuschku.quasseldroid.ui.chat.topic

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TopicFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindTopicFragment(): TopicFragment
}
