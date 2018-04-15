package de.kuschku.quasseldroid.ui.coresettings.chatlist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatlistEditFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindChatListEditFragment(): ChatListEditFragment
}
